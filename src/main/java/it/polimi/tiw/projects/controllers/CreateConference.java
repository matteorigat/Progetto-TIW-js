package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.dao.UserDAO;
import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateConference")
@MultipartConfig
public class CreateConference extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateConference() {
		super();
	}

	public void init() throws ServletException {
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

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		String title = null;
		Timestamp date = new Timestamp(0);
		Time duration = null;
		int guests = 0;

		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			date = Timestamp.valueOf(request.getParameter("date") + " " + request.getParameter("time") + ":00.000000000");
			duration = Time.valueOf(request.getParameter("duration") + ":00");
			guests = Integer.parseInt(request.getParameter("guests"));

			Timestamp now = new Timestamp(System.currentTimeMillis());

			isBadRequest = title.isEmpty() || date.before(now) || guests <= 0;
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}

		UserBean user = (UserBean) session.getAttribute("user");

		Conference conference = new Conference();
		conference.setHostId(user.getId());
		conference.setTitle(title);
		conference.setDate(date);
		conference.setDuration(duration);
		conference.setGuests(guests);
		request.getSession().setAttribute("conference", conference);


		ArrayList<UserBean> users;
		UserDAO userDAO = new UserDAO(connection);
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

		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(users);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
