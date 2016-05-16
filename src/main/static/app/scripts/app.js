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

angular.module('CallForPaper', [
        'ngCookies',
        'ngResource',
        'ngResourceRetries',
        'ngSanitize',
        'ui.router',
        'ngAnimate',
        'ui.bootstrap',
        'ngTagsInput',
        'internationalPhoneNumber',
        'bs-has',
        'pascalprecht.translate',
        'k8LanguagePicker',
        'ngTable',
        'ui-notification',
        'customFilters',
        'relativeDate',
        'matchMedia',
        'angular-loading-bar',
        'ngFx',
        'offClick',
        'hc.marked',
        'mdPreview',
        'LocalStorageModule',
        'cfp.hotkeys',
        'ngAria',
        'restangular',
        'dialogs.main'
    ])
    .config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeSpinner = false;
    }])
    .config(['$httpProvider', function($httpProvider) {
        //Http Intercpetor to check auth failures for xhr requests
        $httpProvider.interceptors.push('authHttpResponseInterceptor');
    }])
    .config(function($stateProvider, $urlRouterProvider, AuthServiceProvider, AppConfigProvider, RestangularProvider, ProfileValidatorProvider) {

        RestangularProvider.setBaseUrl('/api');

        $urlRouterProvider
            .when('', '/dashboard')
            .when('/', '/dashboard')

            .when('/admin/sessions/', '/admin/sessions')

            .when('/admin', '/admin/sessions')
            .when('/admin/', '/admin/sessions')

            .when('/owner', '/owner/config')
            .when('/owner/', '/owner/config')

            .otherwise('/dashboard');

        $stateProvider
            .state('main', {
                abstract: true,
                templateUrl: 'views/header.html',
                resolve: {
                    config: function(Application) {
                        return Application.get().$promise;
                    }
                },
                controller: 'HeaderCtrl',
                controllerAs: 'header'
            })
            .state('admin', {
                parent: 'main',
                url: '/admin',
                abstract: true,
                resolve: {
                    currentUser: ['AuthService', function(AuthService) {
                        return AuthService.getCurrentUser();
                    }]
                },
                views: {
                    'side-menu': {
                        templateUrl: 'views/admin/_side-menu.html',
                        controller: 'AdminMenuCtrl',
                        controllerAs: 'topMenu'
                    },
                    'top-menu': {
                        templateUrl: 'views/admin/_top-menu.html',
                        controller: 'AdminMenuCtrl',
                        controllerAs: 'topMenu'
                    },
                    '': {
                        templateUrl: 'views/admin/admin.html',
                        controller: 'AdminCtrl',
                        resolve: {
                            isAutorizedAdmin: AuthServiceProvider.$get().isAdmin,
                            isConfigured: AppConfigProvider.$get().isConfigured
                        }
                    }
                }
            })
            .state('owner', {
                parent: 'main',
                url: '/owner',
                abstract: true,
                resolve: {
                    currentUser: ['AuthService', function(AuthService) {
                        return AuthService.getCurrentUser();
                    }]
                },
                views: {
                    'side-menu': {
                        templateUrl: 'views/owner/_side-menu.html',
                        controller: 'OwnerMenuCtrl',
                        controllerAs: 'topMenu'
                    },
                    'top-menu': {
                        templateUrl: 'views/owner/_top-menu.html',
                        controller: 'OwnerMenuCtrl',
                        controllerAs: 'topMenu'
                    },
                    '': {
                        templateUrl: 'views/owner/owner.html',
                        controller: 'OwnerCtrl',
                        resolve: {
                            isAutorizedAdmin: AuthServiceProvider.$get().isAdmin,
                            isConfigured: AppConfigProvider.$get().isConfigured
                        }
                    }
                }
            })
            // Config
            .state('owner.config', {
                url: '/config',
                templateUrl: 'views/owner/config.html',
                controller: 'OwnerConfigCtrl'
            })
            // Admins
            .state('owner.admins', {
                url: '/admins',
                templateUrl: 'views/owner/admins.html',
                controller: 'OwnerAdminsCtrl'
            })
            // Fromats
            .state('owner.formats', {
                url: '/formats',
                templateUrl: 'views/owner/formats.html',
                controller: 'OwnerFormatsCtrl'
            })
            // Session
            .state('admin.loading', {
                abstract: true,
                template: '<ui-view/>',
                resolve: {
                    sessionsAll: function(AdminSession) {
                        return AdminSession.query().$promise.then(function(data) {
                            return _.map(data, function(session) {
                                return _.assign(session, { // ugly workaround to be able to filter on speaker fullname
                                    speakerName: [session.speaker.firstname, session.speaker.lastname].join(' ')
                                });
                            });
                        });
                    }
                }
            })
            .state('admin.sessions', {
                url: '/sessions?{format:\d?}',
                parent: 'admin.loading',
                resolve: {
                    tracks: function(TalkService) {
                        return TalkService.tracks.findAll().$promise;
                    },
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    },
                    format: function($stateParams, talkformats) {
                        var format = $stateParams.format;
                        format = format ? parseInt(format, 10) : null;
                        return format && _.find(talkformats, {id: format}) ? format : null;
                    },
                    sessions: function(sessionsAll, format) {
                        return format ? _.filter(sessionsAll, {format: format}) : sessionsAll;
                    },
                    stats: function(AdminStats) {
                        return AdminStats.meter().$promise;
                    }
                },
                templateUrl: 'views/admin/sessions.html',
                controller: 'AdminSessionsCtrl'
            })
            .state('admin.session', {
                url: '/sessions/{id:int}?tab',
                templateUrl: 'views/admin/session.html',
                controller: 'AdminSessionCtrl',
                resolve: {
                    sessionsAll: function(AdminSession) { // TODO Dirty but hard to factorize in a parent state because of the difficulty to keep it up to date
                        return AdminSession.query().$promise;
                    },
                    tracks: function(TalkService) {
                        return TalkService.tracks.findAll().$promise;
                    },
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    },
                    talkId: function($stateParams) {
                        return $stateParams.id || null;
                    },
                    talk: function(AdminSession, talkId, $sanitize) {
                        if (talkId) {
                            return AdminSession.get({
                                id: talkId
                            }).$promise.then(function(session) {
                                session.speaker.bio = $sanitize(session.speaker.bio);
                                return session;
                            });
                        } else {
                            return null;
                        }
                    },
                    nextToRate: function(sessionsAll, AuthService, talkId) {
                        var email = AuthService.user.email;

                        function isUnratedByConnectedUser(session) {
                            return !_.contains(session.voteUsersEmail, email);
                        }

                        return _.find(sessionsAll, function(session) { // first look for the next not rated
                                return session.id > talkId && isUnratedByConnectedUser(session);
                            }) || _.find(sessionsAll, function(session) { // then start again from the beginning
                                return session.id !== talkId && isUnratedByConnectedUser(session);
                            });
                    }
                },
                onEnter: function($state, talkId) {
                    if (!talkId) {
                        $state.go('admin.sessions');
                    }
                }
            })

            // Restricted
            .state('app', {
                parent: 'main',
                abstract: true,
                resolve: {
                    currentUser: ['AuthService', function(AuthService) {
                        return AuthService.getCurrentUser();
                    }],
                    user: function(RestrictedUser) {
                        return RestrictedUser.get().$promise;
                    },
                    isProfileComplete: function(user, ProfileValidator) {
                        return ProfileValidator.isValid(user);
                    }
                },
                views: {
                    'side-menu': {
                        templateUrl: 'views/restricted/_side-menu.html',
                        controller: 'UserMenuCtrl',
                        controllerAs: 'sideMenu'
                    },
                    'top-menu': {
                        templateUrl: 'views/restricted/_top-menu.html',
                        controller: 'UserMenuCtrl',
                        controllerAs: 'topMenu'
                    },
                    '': {
                        template: '<ui-view/>',
                        controller: function($scope) {
                            $scope.header.navBarColorClass = 'navbar-black'; // TODO Pretty dirty…
                        }
                    }
                }
            })
            .state('app.dashboard', {
                url: '/dashboard',
                resolve: {
                    tracks: function(TalkService) {
                        return TalkService.tracks.findAll().$promise;
                    },
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    }
                },
                templateUrl: 'views/restricted/dashboard.html',
                controller: 'DashboardCtrl'
            })
            .state('app.profil', {
                url: '/profil',
                templateUrl: 'views/restricted/profil.html',
                controller: 'ProfilCtrl'
            })

            .state('app.sessions', {
                template: '<ui-view/>',
                resolve: {
                    tracks: function(TalkService) {
                        return TalkService.tracks.findAll().$promise;
                    },
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    },
                    isProfileComplete: ProfileValidatorProvider.isValid()
                }
            })

            .state('app.talks', {
                url: '/talks',
                parent: 'app.sessions',
                templateUrl: 'views/restricted/talks/talks.html',
                abstract: true,
                resolve: {
                    isOpen: AppConfigProvider.$get().isOpen
                }
            })
            .state('app.talks.new', {
                url: '/new',
                templateUrl: 'views/restricted/talks/edit.html',
                resolve: {
                    talk: function() {
                        return {
                            format: null,          // Completely unnecessary, but gives an overview of the
                            name: null,          // structure of a talk object
                            description: null,
                            references: null,
                            difficulty: null,
                            trackId: null,
                            cospeakers: []
                        };
                    }
                },
                controller: 'AppTalksEditCtrl'
            })

            .state('app.drafts', {
                url: '/drafts',
                parent: 'app.sessions',
                abstract: true,
                templateUrl: 'views/restricted/talks/talks.html',
                resolve: {
                    isOpen: AppConfigProvider.$get().isOpen
                }
            })
            .state('app.drafts.edit', {
                url: '/{id:int}/edit',
                templateUrl: 'views/restricted/talks/edit.html',
                resolve: {
                    talk: function(Drafts, $stateParams) {
                        var id = $stateParams.id;
                        if (id) {
                            return Drafts.get(id);
                        } else {
                            return null;
                        }
                    }
                },
                controller: 'AppTalksEditCtrl',
                onEnter: function(talk, $state) {
                    if (!talk) {
                        $state.go('app.dashboard');
                    }
                }
            })

            .state('app.session', {
                url: '/sessions/:id?tab',
                templateUrl: 'views/restricted/session.html',
                controller: 'RestrictedSessionCtrl',
                resolve: {
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    },
                    isCoSession: function() {
                        return false;
                    }
                }
            })
            .state('app.cosession', {
                url: '/cosessions/:id?tab',
                templateUrl: 'views/restricted/session.html',
                controller: 'RestrictedSessionCtrl',
                resolve: {
                    talkformats: function(TalkService) {
                        return TalkService.formats.findAll().$promise;
                    },
                    isCoSession: function() {
                        return true;
                    }
                }
            })

            .state('403', {
                url: '/403',
                templateUrl: '403.html'
            })

            .state('404', {
                url: '/404',
                templateUrl: '404.html'
            });
    })
    .config(['tagsInputConfigProvider', function(tagsInputConfigProvider) {
        tagsInputConfigProvider
            .setDefaults('tagsInput', {
                placeholder: '',
                minLength: 1,
                addOnEnter: true
            })
            .setDefaults('autoComplete', {
                debounceDelay: 200,
                loadOnDownArrow: true,
                loadOnEmpty: true
            });
    }])
    .config(['$translateProvider', function($translateProvider) {
        $translateProvider.useCookieStorage();
    }])
    .config(function(NotificationProvider) {
        NotificationProvider.setOptions({
            delay: 2500,
            startTop: 20,
            startRight: 10,
            verticalSpacing: 20,
            horizontalSpacing: 20,
            positionX: 'right',
            positionY: 'top'
        });
    })
    .run(function(AuthService, $http, $rootScope, $state) {
        AuthService.init();
        $.material.init();

        $rootScope.$on('$stateChangeError', function(event, toState, toParams, fromState, fromParams, error) {

            var rules = {
                'profile.incomplete': function() {
                    $state.transitionTo('app.profil');
                }
            };

            var rule = rules[error];

            if (_.isFunction(rule)) {
                event.preventDefault();
                rule();
            }
        });
    })
    .run(function($templateCache) {
        $templateCache.put('ngTagsInput/tags-input.html',
            '<div class="host" tabindex="-1" ng-click="eventHandlers.host.click()" ti-transclude-append=""><div class="tags" ng-class="{focused: hasFocus}"><ul class="tag-list"><li class="tag-item" ng-repeat="tag in tagList.items track by track(tag)" ng-class="{ selected: tag == tagList.selected }"><ti-tag-item data="tag"></ti-tag-item></li></ul><input class="input form-control" autocomplete="off" ng-model="newTag.text" ng-change="eventHandlers.input.change(newTag.text)" ng-keydown="eventHandlers.input.keydown($event)" ng-focus="eventHandlers.input.focus($event)" ng-blur="eventHandlers.input.blur($event)" ng-paste="eventHandlers.input.paste($event)" ng-trim="false" ng-class="{\'invalid-tag\': newTag.invalid}" ng-disabled="disabled" ti-bind-attrs="{type: options.type, placeholder: options.placeholder, tabindex: options.tabindex, spellcheck: options.spellcheck}" ti-autosize=""></div></div>'
        );
    });
