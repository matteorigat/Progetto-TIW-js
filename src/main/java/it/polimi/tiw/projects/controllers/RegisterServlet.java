package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;

import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public RegisterServlet(){
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Copying all the input parameters in to local variables
        String name;
        String surname;
        String email;
        String username;
        String password;
        String password2;

        try {
            name = StringEscapeUtils.escapeJava(request.getParameter("name"));
            surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
            email = StringEscapeUtils.escapeJava(request.getParameter("email"));
            username = StringEscapeUtils.escapeJava(request.getParameter("username"));
            password = StringEscapeUtils.escapeJava(request.getParameter("password"));
            password2 = StringEscapeUtils.escapeJava(request.getParameter("password2"));

            boolean validEmail = EmailValidator.getInstance().isValid(email); // controllo veridicità sintattica dell'email

            if(!validEmail)
                throw new Exception("Email not valid");

            if (name == null || surname == null || email == null || username == null || password == null || password2 == null || name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || password2.isEmpty()){
                throw new Exception("Missing or empty credential value");
            }

        } catch (Exception e) {
            // for debugging only e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
            return;
        }

        UserDAO userDao = new UserDAO(connection);

        try {
            if(userDao.checkUsername(username)){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username duplicate");
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(!password.equals(password2)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password and Confirm password are different");
            return;
        }

        UserBean userBean = new UserBean();
        userBean.setName(name);
        userBean.setSurname(surname);
        userBean.setEmail(email);
        userBean.setUsername(username);
        userBean.setPassword(password);

        //The core Logic of the Registration application is present here. We are going to insert user data in to the database.
        String userRegistered = userDao.registerUser(userBean);

        if(userRegistered.equals("SUCCESS")){   //On success, you can display a message to user on Home page
            request.getRequestDispatcher("/index.html").forward(request, response);
        }
        else   //On Failure, display a meaningful message to the User.
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in creating an account");
        }
    }
}
