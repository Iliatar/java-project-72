package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.dto.UrlIndexPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    public static void create(Context ctx) {
        try {
            String rawUrl = ctx.formParam("url");
            URI uri = new URI(rawUrl);
            URL parsedUrl = uri.toURL();
            String newUrlName = parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
            if (parsedUrl.getPort() > 0) {
                newUrlName += ":" + parsedUrl.getPort();
            }
            Url newUrl = new Url(newUrlName);

            Optional<Url> storedUrl = UrlRepository.find(newUrl.getName());
            if (storedUrl.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
            } else {
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("successFlag", "true");
            }

            ctx.redirect(NamedRoutes.urlsPath());
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
        }


    }

    public static void index(Context ctx) {
        try {
            List<Url> urls = UrlRepository.getEntities();
            UrlIndexPage page = new UrlIndexPage(urls);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            if (ctx.consumeSessionAttribute("successFlag") == "true") {
                page.setSuccessFlag(true);
            }
            ctx.render("urls/index.jte", model("page", page));
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void show(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
            List<UrlCheck> urlChecks = UrlCheckRepository.getAllUrlChecks(id);
            UrlPage page = new UrlPage(url, urlChecks);
            ctx.render("urls/show.jte", model("page", page));
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void checkUrl(Context ctx) {

    }
}
