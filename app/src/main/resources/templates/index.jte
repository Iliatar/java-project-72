@template.layouts.page(

    content = @`
        <h1>Анализатор страниц</h1>
        <h2>Бесплатно проверяйте сайты на SEO пригодность</h2>
        <form action="/search" method="post">
            <input type="text" placeholder="Ссылка" name="url">
            <input type="submit" name="Проверить">
        </form>
        <p>Пример: https://www.example.com</p>
    `,
)