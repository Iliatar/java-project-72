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

import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UrlController {
    public static void create(Context ctx) throws SQLException {
        URL parsedUrl;
        String rawUrl = ctx.formParam("url");

        try {
            URI uri = new URI(rawUrl);
            parsedUrl = uri.toURL();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        String newUrlName = parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
        if (parsedUrl.getPort() > 0) {
            newUrlName += ":" + parsedUrl.getPort();
        }
        Url newUrl = new Url(newUrlName);
        Optional<Url> storedUrl = UrlRepository.find(newUrl.getName());
        if (storedUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("successFlag", "true");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        UrlIndexPage page = new UrlIndexPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        if (ctx.consumeSessionAttribute("successFlag") == "true") {
            page.setSuccessFlag(true);
        }
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url;
        List<UrlCheck> urlChecks;
        try {
            url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
            urlChecks = UrlCheckRepository.getAllUrlChecks(id);
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        UrlPage page = new UrlPage(url, urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        if (ctx.consumeSessionAttribute("successFlag") == "true") {
            page.setSuccessFlag(true);
        }

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url;

        try {
            url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = \" + id + \" not found"));

            String urlName = url.getName();
            var response = Unirest.get(urlName).asString();

            int statusCode = response.getStatus();
            String title = "";
            String h1 = "";
            String desc = "";

            if (response.isSuccess()) {
                Document body = Jsoup.parse(response.getBody());
                title = body.title();

                if (body.selectFirst("h1") != null) {
                    h1 = body.selectFirst("h1").text();
                }
                if (body.head().selectFirst("meta[name=description]") != null) {
                    desc = body.head().selectFirst("meta[name=description]").attribute("content").getValue();
                }
            }

            UrlCheckRepository.save(new UrlCheck(statusCode, title, h1, desc, id));
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
        } finally {
            Unirest.shutDown();
            ctx.redirect(NamedRoutes.urlPath(id.toString()));
        }
    }
}
