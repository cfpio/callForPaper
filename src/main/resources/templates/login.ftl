<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>cfp.io</title>
	<meta name="description" content="">
	<meta name="viewport" content="width=device-width">

	<link rel="stylesheet" href="/webjars/bootstrap/3.3.6/css/bootstrap.min.css">
	<link rel="stylesheet" href="/webjars/bootstrap-social/5.0.0/assets/css/font-awesome.css">
	<link rel="stylesheet" href="/webjars/bootstrap-social/5.0.0/bootstrap-social.css">
	<link rel="stylesheet" href="/style/login.css">
    <link rel="icon" href="favicon.ico" />
</head>
<body>


<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header page-scroll">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#page-top">CFP.io</a>
        </div>
	</div>
</nav>

<header>
	Please log in
</header>

<div class="container">

	<div class="row">
		<div class="col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-2 main-text">

			<form class="form-signin" role="form" action="/local/login" method="POST">
				<img src="/img/avatar.png" class="hidden-xs">

                <#if error?? && error == "invalidAuth">
				<div class="alert alert-danger">Your e-mail or password is invalid.</div>
                </#if>

                <#if email??>
                <#else>
                    <#assign email = "">
                </#if>

				<input type="text" name="email" value="${email}" class="form-control" placeholder="E-mail" required autofocus>

				<input type="password" name="password" class="form-control" placeholder="Password" required>

				<button class="btn btn-primary btn-block" type="submit">Connection</button>

				<a href="/local/signup"><small>Create account</small></a>

			</form>

		</div>

		<div class="col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-1 main-text">

            <#if error?? && error == "noEmail">
			<div class="alert alert-danger">
				We can't retrieve your e-mail with the selected provider (check if you don't set it private).
				You can use another provider if you want.
			</div>
			</#if>

			<a href="/google/login" class="btn btn-block btn-social btn-google"><span class="fa fa-google"></span> Sign in with Google </a>
			<a href="/github/login" class="btn btn-block btn-social btn-github"><span class="fa fa-github"></span> Sign in with Github </a>
            <a href="/linkedin/login" class="btn btn-block btn-social btn-linkedin"><span class="fa fa-linkedin"></span> Sign in with LinkedIn </a>
            <!-- FIXME we don't get `email` attribute in twitter data, even whe have been whitelisted
            <a href="/twitter/login" class="btn btn-block btn-social btn-twitter"><span class="fa fa-twitter"></span> Se connecter avec Twitter </a> -->

		</div>
	</div>

</div>

<footer>
	Having troubles to register / log in ? <a href="mailto:contact@cfp.io"> Contact us !</a>
</footer>


</body>
</html>
