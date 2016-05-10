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
    .controller('FormCtrl', ['$scope', '$filter', '$translate', 'RestrictedSession', 'RestrictedDraft', 'RestrictedUser', '$state', '$stateParams', '$q', 'RestrictedProfilImage', 'Upload', 'Application', function($scope, $filter, $translate, RestrictedSession, RestrictedDraft, RestrictedUser, $state, $stateParams, $q, RestrictedProfilImage, Upload, Application) {
        // we will store all of our form data in this object
        $scope.formData = {};
        $scope.formData.steps = {};
        $scope.formData.steps.currentStep = 1;
        $scope.formData.sending = false;
        $scope.formData.steps.isValid = [false, false, false];
        $scope.language = $translate.use();

        $scope.formData.speaker = {};
        $scope.formData.speaker.phone = '';
        $scope.formData.session = {};
        $scope.formData.help = {};

        var id = $stateParams.id;
        var parseRow = function(id) {
            // If editing
            if (id !== '') {
                RestrictedDraft.get({
                    id: id
                }).$promise.then(function(draft) {
                    // Parse returned row for view
                    if (draft.added !== undefined) {
                        for (var key in draft) {
                            if (draft.hasOwnProperty(key)) {
                                switch (key) {
                                    case 'bio':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.bio = draft[key];
                                        }
                                        break;
                                    case 'coSpeaker':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.coSpeaker = draft[key];
                                        }
                                        break;
                                    case 'company':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.company = draft[key];
                                        }
                                        break;
                                    case 'description':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.description = draft[key];
                                        }
                                        break;
                                    case 'difficulty':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.difficulty = draft[key];
                                            $scope.hoverDifficulty(draft[key]);
                                        }
                                        break;
                                    case 'email':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.email = draft[key];
                                        }
                                        break;
                                    case 'financial':
                                        if (draft[key] !== null) {
                                            $scope.formData.help.financial = draft[key];
                                        }
                                        break;
                                    case 'firstname':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.firstname = draft[key];
                                        }
                                        break;
                                    case 'hotel':
                                        if (draft[key] !== null) {
                                            $scope.formData.help.hotel = draft[key];
                                        }
                                        break;
                                    case 'hotelDate':
                                        if (draft[key] !== null) {
                                            $scope.formData.help.hotelDate = draft[key];
                                        }
                                        break;
                                    case 'lastname':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.lastname = draft[key];
                                        }
                                        break;
                                    case 'phone':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.phone = draft[key];
                                        }
                                        break;
                                    case 'references':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.references = draft[key];
                                        }
                                        break;
                                    case 'sessionName':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.sessionName = draft[key];
                                        }
                                        break;
                                    case 'social':
                                        if (draft[key] !== null && draft[key] !== '') {
                                            $scope.formData.speaker.socialArray = draft[key].split(', ').map(function(value) {
                                                return {
                                                    text: value
                                                };
                                            });
                                        }
                                        break;
                                    case 'twitter':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.twitter = draft[key];
                                        }
                                        break;
                                    case 'googleplus':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.googleplus = draft[key];
                                        }
                                        break;
                                    case 'github':
                                        if (draft[key] !== null) {
                                            $scope.formData.speaker.github = draft[key];
                                        }
                                        break;
                                    case 'track':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.track = draft[key];
                                        }
                                        break;
                                    case 'type':
                                        if (draft[key] !== null) {
                                            $scope.formData.session.type = draft[key];
                                        }
                                        break;
                                    case 'travel':
                                        if (draft[key] !== null) {
                                            $scope.formData.help.travel = draft[key];
                                        }
                                        break;
                                    case 'travelFrom':
                                        if (draft[key] !== null) {
                                            $scope.formData.help.travelFrom = draft[key];
                                        }
                                        break;
                                }
                            }
                        }

                        // Get profile image / parse it
                        RestrictedUser.query(function(profil) {
                            if (profil !== undefined) {
                                for (var key in profil) {
                                    if (profil.hasOwnProperty(key)) {
                                        switch (key) {

                                            case 'imageProfilKey':
                                                if (profil[key] !== undefined) {
                                                    $scope.formData.speaker.imageProfilKey = profil[key];
                                                }
                                                break;
                                            case 'socialProfilImageUrl':
                                                if (profil[key] !== undefined) {
                                                    $scope.formData.speaker.socialProfilImageUrl = profil[key];
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        });

                    } else {
                        // Not existing
                        $state.go('404');
                    }
                });
            } else {
                // Not editing => new one
                // Get user profil / parse it
                RestrictedUser.query(function(profil) {
                    if (profil !== undefined) {
                        for (var key in profil) {
                            if (profil.hasOwnProperty(key)) {
                                switch (key) {
                                    case 'bio':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.bio = profil[key];
                                        }
                                        break;
                                    case 'company':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.company = profil[key];
                                        }
                                        break;
                                    case 'email':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.email = profil[key];
                                        }
                                        break;
                                    case 'firstname':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.firstname = profil[key];
                                        }
                                        break;
                                    case 'lastname':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.lastname = profil[key];
                                        }
                                        break;
                                    case 'phone':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.phone = profil[key];
                                        }
                                        break;
                                    case 'imageProfilKey':
                                        if (profil[key] !== undefined) {
                                            $scope.formData.speaker.imageProfilKey = profil[key];
                                        }
                                        break;
                                    case 'socialProfilImageUrl':
                                        if (profil[key] !== undefined) {
                                            $scope.formData.speaker.socialProfilImageUrl = profil[key];
                                        }
                                        break;
                                    case 'social':
                                        if (profil[key] !== null && profil[key] !== '') {
                                            $scope.formData.speaker.socialArray = profil[key].split(', ').map(function(value) {
                                                return {
                                                    text: value
                                                };
                                            });
                                        }
                                        break;
                                    case 'twitter':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.twitter = profil[key];
                                        }
                                        break;
                                    case 'googleplus':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.googleplus = profil[key];
                                        }
                                        break;
                                    case 'github':
                                        if (profil[key] !== null) {
                                            $scope.formData.speaker.github = profil[key];
                                        }
                                        break;
                                }
                            }
                        }
                    }
                });
            }
        };
        parseRow(id);

        /**
         * [upload description]
         * @return {promise}
         */
        var upload = function() {
            var deferred = $q.defer();
            if ($scope.formData.speaker.files && $scope.formData.speaker.files.length) {
                // put
                RestrictedProfilImage.getUploadUri().$promise.then(function(respUrl) {
                    var url = respUrl.uri;
                    _.each($scope.formData.speaker.files, function(file) {
                        Upload.upload({
                            url: url,
                            file: file,
                            fileName: 'profil-image',
                            sendFieldsAs: 'form'
                        }).progress(function(evt) {
                            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                            deferred.notify(progressPercentage);
                        }).success(function(data) {
                            deferred.resolve(data);
                        }).error(function(data, status) {
                            deferred.reject(status);
                        });
                    });
                }, function(err) {
                    deferred.reject(err);
                });
            } else {
                deferred.resolve({
                    key: $scope.formData.speaker.imageProfilKey
                });
            }
            return deferred.promise;
        };

        /**
         * update profil
         * @param  {string} profilImageKey
         * @return {promise}
         */
        var updateProfile = function(profilImageKey) {
            var deferred = $q.defer();
            // put
            $scope.formData.speaker.imageProfilKey = profilImageKey.key;
            RestrictedUser.update({}, $scope.formData.speaker, function() {
                deferred.resolve();
            }, function(error) {
                deferred.reject(error);
            });
            return deferred.promise;
        };


        /**
         * Upload profil image then update profil then save talk
         * @param  {Boolean}
         * @return {void}
         */
        $scope.processForm = function() {
            $scope.formData.sending = true;
            $scope.sendError = false;
            $scope.sendErrorClose = false;
            var model = {};
            angular.extend(model, $scope.formData.help);
            angular.extend(model, $scope.formData.speaker);
            angular.extend(model, $scope.formData.session);

            upload().then(function(profilImageKey) {
                updateProfile(profilImageKey).then(function() {
                    if (id !== '') {
                        // put
                        RestrictedSession.update({
                            id: id
                        }, model).$promise.then(function() {
                            $scope.formData.sending = false;
                            $state.go('app.form.result');
                        }, function(error) {
                            $scope.formData.sending = false;
                            if (error.status === 400) {
                                $scope.sendErrorClose = true;
                                return;
                            }
                            $scope.sendError = true;
                        });
                    } else {
                        // save
                        RestrictedSession.save(model).$promise.then(function() {
                            $scope.formData.sending = false;
                            $state.go('app.form.result');
                        }, function(error) {
                            $scope.formData.sending = false;
                            if (error.status === 400) {
                                $scope.sendErrorClose = true;
                                return;
                            }
                            $scope.sendError = true;
                        });
                    }

                }, function() {
                    $scope.formData.sending = false;
                    $scope.sendError = true;
                });
            }, function() {
                $scope.formData.sending = false;
                $scope.sendError = true;
            });
        };

        /**
         * Send the draft to the server
         * @param  {Boolean}
         * @return {void}
         */
        $scope.processSaveForm = function() {
            $scope.formData.sending = true;
            $scope.sendError = false;
            var model = {};
            angular.extend(model, $scope.formData.help);
            angular.extend(model, $scope.formData.speaker);
            angular.extend(model, $scope.formData.session);

            // empty previous completed field
            for (var key in model) {
                if (model[key] === undefined) {
                    model[key] = '';
                }
            }

            if (id !== '') {
                // put
                RestrictedDraft.update({
                    id: id
                }, model).$promise.then(function() {
                    $scope.formData.sending = false;
                    $state.go('app.dashboard');
                }, function() {
                    $scope.formData.sending = false;
                    $scope.sendError = true;
                });
            } else {
                // save
                RestrictedDraft.save(model).$promise.then(function() {
                    $scope.formData.sending = false;
                    $state.go('app.dashboard');
                }, function() {
                    $scope.formData.sending = false;
                    $scope.sendError = true;
                });
            }
        };

        /**
         * Update difficulty label when over
         */
        $scope.hoverDifficulty = function(value) {
            $scope.formData.session.difficultyLabel = ([$filter('translate')('step2.beginner'), $filter('translate')('step2.confirmed'), $filter('translate')('step2.expert')])[value - 1];
        };


        Application.get(function(config) {
            $scope.releaseDate = config.releaseDate;
        });
    }]);
