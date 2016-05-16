/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

angular.module('CallForPaper')
    .config(['$translateProvider', function($translateProvider) {
        $translateProvider.translations('fr-FR', {
            DIALOGS_ERROR: 'Erreur',
            DIALOGS_ERROR_MSG: 'Une erreur inconnue s\'est produite.',
            DIALOGS_CLOSE: 'Fermer',
            DIALOGS_PLEASE_WAIT: 'Veuillez patienter',
            DIALOGS_PLEASE_WAIT_ELIPS: 'Veuillez patienter…',
            DIALOGS_PLEASE_WAIT_MSG: 'Attente de la fin de l\'opération.',
            DIALOGS_PERCENT_COMPLETE: '% Complet',
            DIALOGS_NOTIFICATION: 'Notification',
            DIALOGS_NOTIFICATION_MSG: 'Notification applicative Inconnu.',
            DIALOGS_CONFIRMATION: 'Confirmation',
            DIALOGS_CONFIRMATION_MSG: 'Confirmation requise.',
            DIALOGS_OK: 'OK',
            DIALOGS_YES: 'Oui',
            DIALOGS_NO: 'Non',
            'lang': {
                'fr-FR': 'Français',
                'en-US': 'Anglais'
            },
            'header': {
                'login': 'Se connecter',
                'logout': 'Se déconnecter',
                'dashboard': 'Dashboard',
                'profil': 'Profil',
                'openUntil': 'Ouvert jusqu\'au ',
                'decision': 'Délibération le ',
                'admin': 'Admin'
            },
            'languageModal': {
                'choose': 'Choisissez une langue',
                'search': 'Rechercher une langue...',
                'count1': 'Cette application est proposée en ',
                'count2': 'langues',
                'more': 'plus',
                'close': 'Fermer'
            },
            'dashboard': {
                'createNewSession': 'Créer un nouveau talk',
                'submissionDisabled': 'La soumission de nouvelles candidatures est désactivée.',
                'editionTalks': 'Talks en cours d\'édition',
                'editionCoTalks': 'Talks (en tant que cospeaker) en cours d\'édition',
                'noEditionTalks': 'Vous n\'avez pas de talk en cours d\'édition',
                'sendedTalks': 'Talks envoyés',
                'draftModifiedAt': 'Brouillon modifié le ',
                'modifiedAt': 'modifié le ',
                'postedAt': 'ajouté le ',
                'noSendedTalks': 'Vous n\'avez pas encore proposé de talk',
                'verification': 'Vérification',
                'verificationNeeded': 'Un e-mail a été envoyé à votre adresse, cliquez sur le lien présent dans celui-ci pour valider votre compte.',
                'submittedTalks': 'Nombre de talks soumis : ',
                'sendedCoTalks' :  'Talks (en tant que cospeaker) envoyés'
            },
            'profil': {
                'save': 'Sauvegarder',
                'success': 'Profil sauvegardé',
                'error': 'Erreur lors de l\'enregistrement',
                'avatar': 'Image de profil :',
                'dropImage': 'Déposer l\'image ici',
                'selectImage': 'Parcourir'
            },
            'login': {
                'title': 'Bienvenue sur le Call For Paper du Breizhcamp.',
                'email': 'Email',
                'password': 'Mot de passe',
                'login': 'Se connecter',
                'noAccount': 'Vous n\'avez pas encore de compte ?',
                'signup': 'S\'enregistrer',
                'or': 'Ou',
                'signGoogle': 'Se connecter avec Google',
                'signGithub': 'Se connecter avec Github',
                'wait': 'Veuillez patienter',
                'badCredentials': 'Login ou mot de passe incorrect',
                'alreadyLinked': 'Il existe déjà un utilisateur associé à ce fournisseur'
            },
            'signup': {
                'signup': 'S\'enregistrer',
                'email': 'Email',
                'password': 'Mot de passe',
                'confirmPassword': 'Confirmez le mot de passe',
                'yesAccount': 'Vous avez déjà un compte ?',
                'loginNow': 'Se connecter',
                'emailRequired': 'L\'adresse email est obligatoire',
                'emailPattern': 'Votre adresse email est invalide',
                'passwordRequired': 'Le mot de passe est obligatoire',
                'passwordMinLength': 'Le mot de passe doit contenir au moins 6 caractères',
                'passwordMatch': 'Les mots de passe doivent être identiques',
                'alreadyExists': 'Il existe déjà un utilisateur associé à cette adresse e-mail'
            },
            'talkInfo': {
              'intro': 'Voici quelques informations sur les formats et catégories des talks :',
              'formats': 'Formats',
              'categories': 'Catégories'
            },
            'verify': {
                'title': 'Verification de l\'email',
                'verified': 'Votre email a été vérifié',
                'alreadyVerified': 'Ce compte a déjà été vérifié',
                'notVerified': 'Erreur de vérification'
            },
            talk: {
                edit: {
                    title: {
                        'new': 'Nouveau talk',
                        'update': 'Modification du talk «{{name}}»'
                    },
                    saveDraft: 'Brouillon enregistré.',
                    saveSession: 'Session soumise. Un mail de confirmation vous a été envoyé.'
                },
                difficulty: {
                    '1': 'Débutant',
                    '2': 'Confirmé',
                    '3': 'Expert'
                }
            },
            'step1': {
                'warning': 'Attention les informations enregistrées lors de cette étape modifieront votre profil pour tous les talks déjà proposés.',
                'email': 'Adresse email * :',
                'lastname': 'Nom * :',
                'firstname': 'Prénom * :',
                'language': 'Langue préférée',
                'phone': 'Téléphone :',
                'errorPhone': 'Entrez un numéro de téléphone correct',
                'company': 'Entreprise :',
                'bio': 'Bio * :',
                'hintBio': 'Décrivez vous en quelques mots. Cette description sera utilisée sur le site web.',
                'social': 'Autres URLs (Blog, Linkedin, ...) :',
                'twitter': 'Profil Twitter :',
                'googleplus': 'Profil Google Plus :',
                'github': 'Profil Github :',
                'hintSocial': 'Donnez les liens de vos réseaux sociaux (pour le site web) : Twitter / G+ / Github / Blog.',
                'gender':'Sexe t-shirt',
                'tshirtSize':'Taille t-shirt'
            },
            'step2': {
                'name': 'Nom de la conférence * :',
                'description': 'Description * :',
                'language': 'Langue de présentation :',
                'hintDescription': 'Donnez une description de votre présentation. Elle sera utilisée sur le site web.',
                'references': 'Références ou compléments d\'informations :',
                'hintReferences': 'Y a-t-il des conférences où vous avez déjà fait des présentations ? Si vous pouvez donner un lien vers celle(s)-ci ça serait bien.',
                'difficulty': 'Difficulté * (Débutant, Confirmé, Expert) :',
                'track': 'Catégorie * :',
                'cospeaker': 'Co-conférenciers :',
                'hintCospeaker': 'Si vous n\'êtes pas seul lors de la présentation, donnez l\'email des autres conférenciers. ATTENTION, ceux-ci doivent être préalablement inscrit sur le CFP avec cette même adresse.',
                'cospeakerNotFound' : 'Le cospeaker avec l\'adresse {{value}} n\'existe pas.',
                'beginner': 'Débutant',
                'confirmed': 'Confirmé',
                'expert': 'Expert',
                'tracks': {
                    'cloud': 'Cloud / BigData',
                    'mobile': 'Mobile et objets connectés',
                    'web': 'Web',
                    'discovery': 'Découverte'
                },
                'type': 'Type * :',
                'hintType': 'Une conférence doit durer environ 45 minutes et un codelab 2 heures.',
                'types': {
                    'conference': 'Conférence',
                    'codelab': 'Codelab'
                },
                'hintTrack': 'Choisissez la catégorie dans laquelle vous pensez que votre conférence se situe.'
            },
            'step3': {
                'header1': 'Vous pouvez renseigner ici les informations nécessaires pour votre venue. Ces informations seront minutieusement étudiées pour notre décision. Ne choisissez oui que si vous en avez besoin.',
                'header2': 'Un petit déjeuner et un déjeuner sont offerts le jour de l\'évènement.',
                'financial': 'Avez-vous besoin d’une aide financière ? * :',
                'labelTravel': 'Voyage :',
                'travel': 'J\'ai besoin d’une aide financière pour le voyage.',
                'place': 'D’où venez vous ? :',
                'labelHotel': 'Hébergement',
                'hotel': 'J\'ai besoin d’une aide financière pour l’hotel.',
                'date': 'Pour quelle(s) date(s) ? :',
                'sendError': 'Erreur lors de l\'envoi veuillez réessayer',
                'sryClosedSince': 'Désolé, le CFP est fermé depuis le '
            },
            'confirmModal': {
                'title': 'Confirmation',
                'text': 'Êtes-vous sûr de vouloir envoyer ce talk ? Une fois envoyé, vous ne serez pas en mesure de le modifier.',
                'textDelete': 'Êtes-vous sûr de vouloir suprimer cette session ? Une fois supprimée, vous ne serez pas en mesure de la restaurer.',
                'confirm': 'Envoyer',
                'confirmDelete': 'Supprimer',
                'cancel': 'Annuler'
            },
            'previewModal': {
                'title': 'Prévisualisation',
                'confirm': 'OK',
                'nothingToPreview': 'Rien à prévisualiser',
                'mdSupported': 'Markdown supporté'
            },
            'editModal': {
                'title': 'Edition',
                'confirm': 'Editer',
                'cancel': 'Annuler'
            },
            'deleteModal': {
                'title': 'Confirmation',
                'text': 'Êtes-vous sûr de vouloir suprimer ce commentaire ?',
                'confirm': 'Supprimer',
                'cancel': 'Annuler'
            },
            'steps': {
                'saveAsDraft': 'Enregistrer brouillon',
                'previous': 'Etape précédente',
                'next': 'Etape suivante',
                'validate': 'Valider',
                'close': 'Fermer',
                'yes': 'Oui',
                'no': 'Non',
                'step': 'Etape',
                'done': 'Terminé !',
                'required': '* Champ requis.'
            },
            'result': {
                'success': 'Bravo !',
                'successMessage': 'Votre présentation a été envoyée. Vous recevrez bientôt un email de confirmation. Nous vous recontacterons dès que nous aurons fait notre choix.',
                'goToHome': 'Retour à la page principale'
            },
            'contact': {
                'message': 'Message',
                'validate': 'Valider',
                'edit': 'Editer',
                'hint': 'Un oubli, une question ? Postez votre message ici, un administrateur vous répondra dès que possible.'
            },
            'admin': {
                'logout': 'Se déconnecter',
                'allowSubmissions': 'Autoriser les utilisateurs à poster de nouveaux talks ?',
                'session': 'Talk',
                'sessions': 'Talks',
                'administration': 'Administration',
                'toggle': 'Ouvrir le volet',
                'clearSorting': 'Annuler le tri',
                'clearFilter': 'Annuler les filtres',
                'export': 'Exporter',
                'speaker': 'Conférencier',
                'title': 'Titre',
                'difficulty': 'Difficulté',
                'track': 'Catégorie',
                'trackModified': 'Talk modifié.',
                'modify': 'Modifier',
                'description': 'Description',
                'mean': 'Moyenne',
                'date': 'Date',
                'deliberation': 'Délibération',
                'contact': 'Contact',
                'commentaries': 'Commentaires',
                'message': 'Message',
                'votes': 'Votes',
                'you': 'Vous',
                'financialHelp': 'Aide financière',
                'deleteSession': 'Supprimer la session',
                'validated': 'Sessions validées',
                'draft': 'Sessions en cours d\'édition',
                'lastModification': 'Dernière activité',
                'never': 'Jamais',
                'noVote': 'Sans avis',
                'none': 'Aucune',
                'hate': 'Rejeté',
                'love': 'Coup de coeur',
                'next': 'Talk suivant',
                'main': 'Main page',
                'previous': 'Talk précédente',
                'notReviewedYet': 'Pas encore notés',
                'edit': 'Editer',
                'delete': 'Supprimer',
                'allType': 'Tous',
                'newMessages': 'Nouveaux messages',
                nextToRate: 'Prochain talk à noter',
                'community': 'Nom de la communautée organisatrice',
                'eventName': 'Nom de l\'événement',
                'decisionDate': 'Date de fin des votes',
                'releaseDate': 'Date de fin des soumissions',
                'startDate': 'Date du début de l\'événement',
                'configCfp': 'Configuration du CFP',
                'btn.save': 'Sauvegarder'
            },
            'owner': {
            	'admins': 'Admins',
            	'config': 'Configuration',
            	'hintAdmins': 'Ajouter les emails des admins',
            	'adminsSuccess': 'La liste des admins a été mise à jour',
                'formats': 'Formats',
                'hintFormats': 'Définissez les formats des sessions de votre événement'

            },
            'config': {
                'logout': 'Se déconnecter',
                'login': 'Se connecter',
                'config': 'Configuration',
                'linkMyAccount': 'Lier mon compte Google Drive avec Call For Paper',
                'configurationNeeded': 'L\'administrateur doit configurer l\'application avant que vous puissiez l\'utiliser',
                'success': 'Votre compte est maintenant lié',
                'error': 'Error lors de la configuration',
                'configureLink': 'Configurer l\'application'
            },
            'error': {
                'backendcommunication': 'Désolé, il y a eu un problème avec le serveur distant',
                'noInternet': 'Désolé, il y a eu une problème de connexion, êtes vous connecté à internet ?'
            },
            'just_now': 'à l\'instant',
            'seconds_ago': 'il y a {{time}} secondes',
            'a_minute_ago': 'il y a une minute',
            'minutes_ago': 'il y a {{time}} minutes',
            'an_hour_ago': 'il y a une heure',
            'hours_ago': 'il y a {{time}} heures',
            'a_day_ago': 'hier',
            'days_ago': 'il y a {{time}} jours',
            'a_week_ago': 'il y a une semaine',
            'weeks_ago': 'il y a {{time}} semaines',
            'a_month_ago': 'il y a un mois',
            'months_ago': 'il y a {{time}} mois',
            'a_year_ago': 'il y a un an',
            'years_ago': 'il y a {{time}} ans',
            'over_a_year_ago': 'il y a plus d\'un an',
            'seconds_from_now': 'dans une seconde',
            'a_minute_from_now': 'dans une minute',
            'minutes_from_now': 'dans {{time}} minutes',
            'an_hour_from_now': 'dans une heure',
            'hours_from_now': 'dans {{time}} heures',
            'a_day_from_now': 'demain',
            'days_from_now': 'dans {{time}} jours',
            'a_week_from_now': 'dans une semaine',
            'weeks_from_now': 'dans {{time}} semaine',
            'a_month_from_now': 'dansun mois',
            'months_from_now': 'dans {{time}} mois',
            'a_year_from_now': 'dans un an',
            'years_from_now': 'dans {{time}} ans',
            'over_a_year_from_now': 'dans plus d\'un an'
        });
        $translateProvider.translations('en-US', {
            DIALOGS_ERROR: 'Error',
            DIALOGS_ERROR_MSG: 'An unknown error has occurred.',
            DIALOGS_CLOSE: 'Close',
            DIALOGS_PLEASE_WAIT: 'Please Wait',
            DIALOGS_PLEASE_WAIT_ELIPS: 'Please Wait…',
            DIALOGS_PLEASE_WAIT_MSG: 'Waiting on operation to complete.',
            DIALOGS_PERCENT_COMPLETE: '% Complete',
            DIALOGS_NOTIFICATION: 'Notification',
            DIALOGS_NOTIFICATION_MSG: 'Unknown application notification.',
            DIALOGS_CONFIRMATION: 'Confirmation',
            DIALOGS_CONFIRMATION_MSG: 'Confirmation required.',
            DIALOGS_OK: 'OK',
            DIALOGS_YES: 'Yes',
            DIALOGS_NO: 'No',
            'lang': {
                'fr-FR': 'French',
                'en-US': 'English'
            },
            'header': {
                'login': 'Login',
                'logout': 'Logout',
                'dashboard': 'Dashboard',
                'profil': 'Profil',
                'openUntil': 'Open until the ',
                'decision': 'Decision the ',
                'admin': 'Admin'
            },
            'languageModal': {
                'choose': 'Choose a langage',
                'search': 'Search langages...',
                'count1': 'This application is offered in',
                'count2': 'langages',
                'more': 'more',
                'close': 'Close'
            },
            'dashboard': {
                'createNewSession': 'Create a new talk',
                'submissionDisabled': 'Submitting new talks is disabled.',
                'editionTalks': 'Talks available for editing',
                'editionCoTalks': 'Cospeaker Talks available for editing',
                'noEditionTalks': 'You don\'t have any draft',
                'sendedTalks': 'Submitted talks',
                'draftModifiedAt': 'Draft modified the ',
                'modifiedAt': 'modified the ',
                'postedAt': 'submited the ',
                'noSendedTalks': 'You don\'t have submitted any talk yet',
                'verification': 'Verification',
                'verificationNeeded': 'An email has been sent to your address, click the link in it to confirm your account.',
                'submittedTalks': 'Submitted talks count: ',
                'sendedCoTalks' : 'Submitted cospeaker talks'
            },
            'profil': {
                'save': 'Save',
                'success': 'Profil saved',
                'error': 'Error saving the profil',
                'avatar': 'Profil picture :',
                'hintAvatar': 'Select a picture for the website (least 360x360 and 5MB max)',
                'dropImage': 'Drop image here',
                'selectImage': 'Browse'
            },
            'login': {
                'email': 'Email',
                'password': 'Password',
                'login': 'Login',
                'noAccount': 'Don\'t have an account yet?',
                'signup': 'Signup',
                'or': 'Or',
                'signGoogle': 'Sign in with Google',
                'signGithub': 'Sign in with Github',
                'wait': 'Please wait',
                'badCredentials': 'Incorrect login or password',
                'alreadyLinked': 'There is already a user associated with this  provider'
            },
            'signup': {
                'signup': 'Sign up',
                'email': 'Email',
                'password': 'Password',
                'confirmPassword': 'Confirm Password',
                'yesAccount': 'Already have an account?',
                'loginNow': 'Log in now',
                'emailRequired': 'Your email address is required.',
                'emailPattern': 'Your email address is invalid.',
                'passwordMinLength': 'Password must be at least 6 characters long',
                'passwordRequired': 'Password is required.',
                'passwordMatch': 'Password must match.',
                'alreadyExists': 'There is already a user associated with this email'
            },
            'verify': {
                'title': 'Email ',
                'verified': 'Your email has been verified',
                'alreadyVerified': 'This account is already verified',
                'notVerified': 'Errror during verification'
            },
            'talkInfo': {
              'intro': 'Here some information on talk\'s format and talk\'s categories :',
              'formats': 'Formats',
              'categories': 'Categories'
            },
            talk: {
                edit: {
                    title: {
                        'new': 'New talk',
                        'update': 'Modification of talk "{{name}}"'
                    },
                    saveDraft: 'Draft saved.',
                    saveSession: 'Session submitted. A confirmation email has been sent to you.'
                },
                difficulty: {
                    '1': 'Beginner',
                    '2': 'Confirmed',
                    '3': 'Expert'
                }
            },
            'step1': {
                'warning': 'Warning ! The informations given during this step will also be used for the talks already submitted.',
                'email': 'Email *:',
                'lastname': 'Name *:',
                'firstname': 'Firstname *:',
                'phone': 'Phone:',
                'errorPhone': 'Please enter a correct phone number',
                'company': 'Company:',
                'bio': 'Bio *:',
                'hintBio': 'Describe yourself with a few words. This description will be use to fill the website.',
                'social': 'Other URLs (Blog, Linkedin, ...) :',
                'twitter': 'Twitter Profil :',
                'googleplus': 'Google Plus Profil:',
                'github': 'Github Profil :',
                'hintSocial': 'Give us your socials networks data (for the website) : Twitter / G+ / Github / Blog.',
                'gender': 'T-shirt gender',
                'tshirtSize': 'T-shirt size'
            },
            'step2': {
                'name': 'Session name *',
                'description': 'Description *:',
                'hintDescription': 'Give a description of your talk. This description will be used to fill the website.',
                'references': 'References or complement informations :',
                'hintReferences': 'Is there any conferences where you have already spoken ? If you could give a link to the presentation, it\'s better.',
                'complement': 'Recommendation and additional information:',
                'difficulty': 'Difficulty * (Beginner, Confirmed, Expert):',
                'track': 'Track *:',
                'cospeaker': 'Co-speaker:',
                'hintCospeaker': 'If you are not alone on stage, give the co-speaker email. WARNING : cospeakers must have create an account with this email before.',
                'cospeakerNotFound' : 'The cospeaker with email {{value}} doesn\'t exist.',
                'beginner': 'Beginner',
                'confirmed': 'Confirmed',
                'expert': 'Expert',
                'tracks': {
                    'cloud': 'Cloud / BigData',
                    'mobile': 'Mobile and internet of things',
                    'web': 'Web',
                    'discovery': 'Discovery'
                },
                'type': 'Type *:',
                'hintType': 'A conference must last about 45 minutes and a codelab about 2 hours.',
                'types': {
                    'conference': 'Conference',
                    'codelab': 'Codelab'
                },
                'hintTrack': 'Choose the track where you think your talk will be place.'
            },
            'step3': {
                'header1': 'Here is all the informations relatives to your venue. The following informations will be carefuly study for our decision. So please select Yes, only if needed.',
                'header2': 'Breakfast and lunch is offered the days of the event.',
                'financial': 'Do you need financial help ? *:',
                'labelTravel': 'Travel',
                'travel': 'I need financial help for the trip.',
                'date': 'For whitch date(s) ?:',
                'labelHotel': 'Housing',
                'hotel': 'I need financial help for the hotel.',
                'place': 'Where are you coming from ?:',
                'sendError': 'An error occurred during the submission, please retry.',
                'sryClosedSince': 'Sorry, submissions are closed since the '
            },
            'confirmModal': {
                'title': 'Confirmation',
                'text': 'Do you really want to submit this talk ? Once sent you will not be able to edit it.',
                'textDelete': 'Do you really want to delete this session ? Once done you will not be able to recover it.',
                'confirm': 'OK',
                'confirmDelete': 'Delete',
                'cancel': 'Cancel'
            },
            'previewModal': {
                'title': 'Preview',
                'confirm': 'OK',
                'nothingToPreview': 'Nothing to preview',
                'mdSupported': 'Markdown supported'
            },
            'editModal': {
                'title': 'Edition',
                'confirm': 'Edit',
                'cancel': 'Cancel'
            },
            'deleteModal': {
                'title': 'Confirmation',
                'text': 'Do you really want to delete this comment ?',
                'confirm': 'Delete',
                'cancel': 'Cancel'
            },
            'steps': {
                'saveAsDraft': 'Save as draft',
                'previous': 'Previous step',
                'next': 'Next step',
                'validate': 'Submit',
                'close': 'Close',
                'yes': 'Yes',
                'no': 'No',
                'step': 'Step',
                'done': 'Done !',
                'required': '* Required field.'
            },
            'result': {
                'success': 'Well done !',
                'successMessage': 'Your talk has been send. You will soon receive a confirmation email. We will contact you as soon as we will make our decision.',
                'goToHome': 'Back to main page'
            },
            'contact': {
                'message': 'Message',
                'validate': 'Submit',
                'edit': 'Edit',
                'hint': 'A question ? A mistake ? Submit your question here, an administrator will answer as soon as possible.'
            },
            'admin': {
                'logout': 'Logout',
                'allowSubmissions': 'Allow users to submit new talks ?',
                'session': 'Talk',
                'sessions': 'Talks',
                'administration': 'Administration',
                'toggle': 'Toggle navigation',
                'clearSorting': 'Clear sorting',
                'clearFilter': 'Clear filters',
                'export': 'Export',
                'title': 'Title',
                'difficulty': 'Difficulty',
                'track': 'Track',
                'trackModified': 'Track edited.',
                'modify': 'Change',
                'description': 'Description',
                'mean': 'Mean',
                'date': 'Date',
                'deliberation': 'Deliberation',
                'contact': 'Contact',
                'commentaries': 'Commentaries',
                'message': 'Message',
                'votes': 'Votes',
                'you': 'You',
                'financialHelp': 'Financial Help',
                'deleteSession': 'Delete session',
                'validated': 'Published talks',
                'draft': 'Talks being edited',
                'speaker': 'Speaker',
                'lastModification': 'Last activity',
                'never': 'Never',
                'noVote': 'Without opinion',
                'none': 'None',
                'hate': 'Rejected',
                'love': 'Favorite',
                'next': 'Next talk',
                'main': 'Page principale',
                'previous': 'Previous talk',
                'notReviewedYet': 'Not reviewed yet',
                'edit': 'Edit',
                'delete': 'Delete',
                'allType': 'All',
                'newMessages': 'New messages',
                nextToRate: 'Next talk to rate'
            },
            'owner': {
            	'admins': 'Admins',
            	'config': 'Configuration',
            	'hintAdmins': 'Enter admins email',
            	'adminsSuccess': 'Admin list updated'
            },
            'config': {
                'login': 'Login',
                'logout': 'Logout',
                'config': 'Configuration',
                'linkMyAccount': 'Link my Google Drive account with Call For Paper',
                'configurationNeeded': 'Administrator must configure the application before you use it',
                'success': 'Your account has been linked',
                'error': 'Error linking your account with Google Drive',
                'configureLink': 'Configure the application'
            },
            'error': {
                'backendcommunication': 'Sorry, a problem occure with the server',
                'noInternet': 'Sorry, it seems that your are not connected to internet'
            },
            'just_now': 'just now',
            'seconds_ago': '{{time}} seconds ago',
            'a_minute_ago': 'a minute ago',
            'minutes_ago': '{{time}} minutes ago',
            'an_hour_ago': 'an hour ago',
            'hours_ago': '{{time}} hours ago',
            'a_day_ago': 'yesterday',
            'days_ago': '{{time}} days ago',
            'a_week_ago': 'a week ago',
            'weeks_ago': '{{time}} weeks ago',
            'a_month_ago': 'a month ago',
            'months_ago': '{{time}} months ago',
            'a_year_ago': 'a year ago',
            'years_ago': '{{time}} years ago',
            'over_a_year_ago': 'over a year ago',
            'seconds_from_now': '{{time}} seconds from now',
            'a_minute_from_now': 'a minute from now',
            'minutes_from_now': '{{time}} minutes from now',
            'an_hour_from_now': 'an hour from now',
            'hours_from_now': '{{time}} hours from now',
            'a_day_from_now': 'tomorrow',
            'days_from_now': '{{time}} days from now',
            'a_week_from_now': 'a week from now',
            'weeks_from_now': '{{time}} weeks from now',
            'a_month_from_now': 'a month from now',
            'months_from_now': '{{time}} months from now',
            'a_year_from_now': 'a year from now',
            'years_from_now': '{{time}} years from now',
            'over_a_year_from_now': 'over a year from now'
        });
        $translateProvider.preferredLanguage('fr-FR');
    }]);
