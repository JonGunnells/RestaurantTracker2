import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    HashMap m = new HashMap();
                    if (username == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        User User = users.get(username);
                        m.put("messages", User.messages);
                        return new ModelAndView(m, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String pass = request.queryParams("password");
                    if (name == null || pass == null) {
                        throw new Exception("name or pass not sent");
                    }

                    User User = users.get(name);
                    if (User == null) {
                        User = new User(name, pass);
                        users.put(name, User);
                    } else if (!pass.equals(User.password)) {
                        throw new Exception("wrong password");
                    }

                    Session session = request.session();
                    session.attribute("username", name);

                    response.redirect("/");
                    return "";
                }

        );
        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }

                    String msg = request.queryParams("message");
                    if (msg == null) {
                        throw new Exception("invalid form fields");
                    }

                    User User = users.get(username);
                    if (User == null) {
                        throw new Exception("User does not exist");
                    }

                    Message r = new Message(msg);
                    User.messages.add(r);

                    response.redirect("/");
                    return "";

                }

        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    int id = Integer.valueOf(request.queryParams("id"));

                    User User = users.get(username);
                    if (id <= 0 || id - 1 >= User.messages.size() - 1) ;
                    {

                        User.messages.remove(id - 1);
                    }
                    response.redirect("/");
                    return "";
                }

        );
        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");

                    }
                    int id = Integer.valueOf(request.queryParams("messageEdit"));
                    String text = request.queryParams("messageField");

                    User User = users.get(username);
                    if (id <= 0 || id - 1 >= User.messages.size() - 1);
                    {
                        Message m = User.messages.get(id - 1);
                        m.message = text;
                    }
                    response.redirect("/");
                    return "";
                }
        );
    }
}





