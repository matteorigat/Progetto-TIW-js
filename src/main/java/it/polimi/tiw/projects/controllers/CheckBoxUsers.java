package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.dao.ConferenceDAO;
import it.polimi.tiw.projects.dao.GuestDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckBoxUsers")
@MultipartConfig
public class CheckBoxUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckBoxUsers() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		Conference conference = (Conference) session.getAttribute("conference");
		if (conference == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Not possible to recover conference");
			return;
		}

		String[] checkBoxArray = null;
		boolean isBadRequest = false;
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

			request.getSession().removeAttribute("conference");

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("success");

		} else {

			request.getSession().removeAttribute("conference");

			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("error");
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
