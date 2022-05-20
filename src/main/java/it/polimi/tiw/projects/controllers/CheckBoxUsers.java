package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.dao.ConferenceDAO;
import it.polimi.tiw.projects.dao.GuestDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckBoxUsers")
public class CheckBoxUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	private int attempt = 0;

	public CheckBoxUsers() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		Conference conference = (Conference) request.getSession().getAttribute("conference");
		if (conference == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}

		String[] checkBoxArray = null;
		Boolean isBadRequest = false;
		try {
			checkBoxArray = request.getParameterValues("userscheckbox");

		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}

		if (isBadRequest || checkBoxArray.length < 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		if(conference.getGuests() >= checkBoxArray.length) {
			GuestDAO guestDAO = new GuestDAO(connection);
			ConferenceDAO conferenceDAO = new ConferenceDAO(connection);
			try {
				conferenceDAO.createConference(conference);
				int conferenceId = conferenceDAO.findLastConferenceByUser(conference.getHostId());
				guestDAO.registerGuests(checkBoxArray, conferenceId);

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			// Redirect to the Home page
			attempt = 0;
			request.getSession().removeAttribute("conference");
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);

		} else {

			if(attempt >= 2){
				// Redirect to Cancellazione
				attempt = 0;
				request.getSession().removeAttribute("conference");

				String path = "/WEB-INF/Cancellazione";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
			} else {

				ArrayList<UserBean> users;
				UserDAO userDAO = new UserDAO(connection);
				UserBean user = (UserBean) session.getAttribute("user");
				try {
					users = userDAO.getUsers(user.getId());
					if (users == null) {
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover users");
					return;
				}

				for(int i=0; i<checkBoxArray.length; i++)
					for(UserBean ub: users)
						if(Integer.parseInt(checkBoxArray[i]) == ub.getId())
							ub.setChecked(true);

				// Redirect to the Anagrafica page and add users to the parameters
				attempt++;
				String path = "/WEB-INF/Anagrafica";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("users", users);
				ctx.setVariable("attempt", 3 - attempt);
				ctx.setVariable("surplusGuests",checkBoxArray.length - conference.getGuests());
				templateEngine.process(path, ctx, response.getWriter());
			}
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
