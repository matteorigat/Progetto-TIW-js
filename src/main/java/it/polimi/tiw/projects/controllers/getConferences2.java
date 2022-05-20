package it.polimi.tiw.projects.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.ConferenceDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/getConferences2")
public class getConferences2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public getConferences2() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		UserBean user = (UserBean) session.getAttribute("user");
		ConferenceDAO conferenceDAO = new ConferenceDAO(connection);
		List<Conference> conferences2;

		try {
			conferences2 = conferenceDAO.findConference2ByUser(user.getId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover conferences");
			return;
		}

		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy MMM dd HH:mm:ss").create();
		String json = gson.toJson(conferences2);


		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
