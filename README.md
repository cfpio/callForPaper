
# Call For Papers


<img src="https://raw.githubusercontent.com/SII-Nantes/callForPaper/master/readme/screenshot.png" alt="alt text" width="100%">

## Features

 - Spring Boot Application
 - AngularJS Front-end
 - JWT Token
 - Localization (fr, en)
 - Material design

### User Panel

 - Google, Github OAuth authentification
 - Account register (with email confirmation)
 - Talks submission (with email confirmation)
 - Save/delete draft

### Admin Panel

 - Sort talks (by rate, date, track...)
 - Filter talks (by track, talker name, description...)
 - Rate talk
 - Comment talk

## Setup

*Change **127.0.0.1:8080** to your application domain for production*

### Obtaining OAuth Keys

<img src="http://images.google.com/intl/en_ALL/images/srpr/logo6w.png" width="150">

- Visit [Google Cloud Console](https://cloud.google.com/console/project)
- Click **CREATE PROJECT** button
- Enter *Project Name*, then click **CREATE**
- Then select *APIs & auth* from the sidebar and click on *Credentials* tab
- Click **CREATE NEW CLIENT ID** button
 - **Application Type**: Web Application
 - **Authorized Javascript origins**: *http://127.0.0.1:8080*
 - **Authorized redirect URI**: *http://127.0.0.1:8080*

**Note:** Make sure you have turned on **Contacts API**, **Google+ API** and **Drive API** in the *APIs* tab.

<hr>

<img src="https://www.cloudamqp.com/images/blog/github.png" height="70">

- Visit [Github developers settings](https://github.com/settings/developers)
- Click **REGISTER NEW APPLICATION** button
 - **Application name**: *Application name*
 - **Homepage URL**: *http://127.0.0.1:8080*
 - **Authorization callback URL**: *http://127.0.0.1:8080*

### Obtaining reCAPTCHA Keys

<img src="https://www.gstatic.com/recaptcha/admin/logo_recaptcha_color_24dp.png" height="70">

- Visit [reCaptcha panel](https://www.google.com/recaptcha/admin)
 - **Domaines**: *127.0.0.1:8080*
- Click **Save** button

### Edit CFP Settings

Settings are provided by `application.properties` file or by JVM arguments.

Development settings are in `config/application.properties` of this repo.
Global, embedded, settings are defined in `src/main/resources/application.properties`.

Please read [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for more information:

You also need to edit `src/main/static/app/scripts/app.js` to add your providers tokens:

```javascript
  .constant('Config', {
    'recaptcha': 'yourRecaptchaPublicToken',
    'googleClientId': 'yourGoogleClientId',
    'githubClientId': 'yourGithubClientId'
  })
```

## Deployment

* NodeJS is required to run frontend build tools. The build has been tested with version 4.2.2. Its installation is
included in the Maven build. However, if you want to contribute to frontend, you may install it by yourself to be able
to use frontend build tools from the command line.

### Development

You need a MySQL database to start the application. If you have Docker, you can launch one for development with

```bash
$ mysql-dev.sh
```

#### Backend development

If you intend to contribute to backend exclusively and don't want to deal with the NodeJS tools necessary for frontend
development and build, you can start the application with the following command:

```bash
$ mvn -Prelease spring-boot:run
```

You can start `fr.sii.Application#main()` on your favorite IDE too.

#### Frontend development

Frontend contributors can use usual frontend build tools from the command line:

* Gulp and Bower are required to build. You can install them with:
    ```bash
    $ npm install -g gulp bower
    ```

* To install build tools dependencies specified in `package.json`, you have to run the following in project root folder:
    ```bash
    $ npm install
    ```

* To install project third party libraries specified in `bower.json`, you have to run the following in project root folder:
    ```bash
    $ bower install
    ```

A Gulp task `gulp serve` is available to facilitate contributions to the front code. It starts a
web server on port 3000 for files in `src/main/static/app` directory. Livereload is configured and
requests to the backend are forwarded to the AppEngine development server (`http://127.0.0.1:8080`) thanks to an proxy.

    ```bash
    $ gulp serve # or gulp serve:dist for minified version of front files, but no livereload in this case
    ```

## Usage :

### Manage admin users :

- Visit [Google Cloud Console](https://cloud.google.com/console/project)
- Select your project
- Click **Permissions** button
- Click **Add member** button :
 - **Email**: New member e-mail
 - **Can view**

### App entry points :

 - http://127.0.0.1:8080/ : User login page (create new talks)

 - http://127.0.0.1:8080/#/admin : Admin panel (rating, comment...)
