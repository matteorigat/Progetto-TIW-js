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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

	private int attempt = 0;

	public CheckBoxUsers() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Not possible to recover conference");
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
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

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("success");

		} else {

			if(attempt >= 2){
				// Redirect to Cancellazione
				attempt = 0;
				request.getSession().removeAttribute("conference");

				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("error");
			} else {

				ArrayList<UserBean> users;
				UserDAO userDAO = new UserDAO(connection);
				UserBean user = (UserBean) session.getAttribute("user");
				try {
					users = userDAO.getUsers(user.getId());
					if (users == null) {
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
						response.getWriter().println("Resource not found");
						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Not possible to recover users");
					return;
				}

				for(int i=0; i<checkBoxArray.length; i++)
					for(UserBean ub: users)
						if(Integer.parseInt(checkBoxArray[i]) == ub.getId())
							ub.setChecked(true);

				// Redirect to the Anagrafica page and add users to the parameters
				attempt++;

				Gson gson = new GsonBuilder().create();
				String json = gson.toJson(users);

				response.setStatus(HttpServletResponse.SC_CONTINUE);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
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
