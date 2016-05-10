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

angular.module('CallForPaper').controller('ProfilCtrl', function($scope, RestrictedUser, translateFilter, Notification, $state) {
    $scope.formData = {};
    $scope.formData.phone = '';
    $scope.formData.imageProfilKey = null;

    $scope.$watch(function() {
        return $scope.form && $scope.form.lastname.$valid && $scope.form.firstname.$valid && ($scope.form.phone.$valid || $scope.formData.phone === '') && $scope.form.company.$valid && $scope.form.bio.$valid && $scope.form.social.$valid && $scope.form.twitter.$valid && $scope.form.googleplus.$valid && $scope.form.github.$valid;
    }, function(isValid) {
        $scope.formData.isValid = isValid;
    });

    $scope.$watch(function() {
        if ($scope.formData.socialArray !== undefined) {
            return $scope.formData.socialArray.length;
        }
    }, function() {
        if ($scope.formData.socialArray !== undefined) {
            $scope.formData.social = $scope.formData.socialArray.map(function(elem) {
                return elem.text;
            }).join(', ');
        }
    });

    /**
     * Get current user profil
     * @return {RestrictedUser}
     */
    RestrictedUser.get(function(profil) {
        if (profil !== undefined) {
            // Parse for view model
            for (var key in profil) {
                if (profil.hasOwnProperty(key)) {
                    switch (key) {
                        case 'bio':
                            if (profil[key] !== null) {
                                $scope.formData.bio = profil[key];
                            }
                            break;
                        case 'company':
                            if (profil[key] !== null) {
                                $scope.formData.company = profil[key];
                            }
                            break;
                        case 'firstname':
                            if (profil[key] !== null) {
                                $scope.formData.firstname = profil[key];
                            }
                            break;
                        case 'lastname':
                            if (profil[key] !== null) {
                                $scope.formData.lastname = profil[key];
                            }
                            break;
                        case 'phone':
                            if (profil[key] !== null) {
                                $scope.formData.phone = profil[key];
                            }
                            break;
                        case 'email':
                            if (profil[key] !== null) {
                                $scope.email = profil[key];
                            }
                            break;
                        case 'imageProfilKey':
                            if (profil[key] !== undefined) {
                                $scope.formData.imageProfilKey = profil[key];
                            }
                            break;
                        case 'imageProfilURL':
                            if (profil[key] !== undefined) {
                                $scope.formData.imageProfilURL = profil[key];
                            }
                            break;
                        case 'social':
                            if (profil[key] !== null && profil[key] !== '') {
                                $scope.formData.socialArray = profil[key].split(', ').map(function(value) {
                                    return {
                                        text: value
                                    };
                                });
                            }
                            break;
                        case 'twitter':
                            if (profil[key] !== null) {
                                $scope.formData.twitter = profil[key];
                            }
                            break;
                        case 'googleplus':
                            if (profil[key] !== null) {
                                $scope.formData.googleplus = profil[key];
                            }
                            break;
                        case 'github':
                            if (profil[key] !== null) {
                                $scope.formData.github = profil[key];
                            }
                            break;
                    }
                }
            }
        }
    });

    /**
     * update profil
     * @type {string} profile img blob key
     */
    $scope.sendError = false;
    $scope.sendSuccess = false;
    $scope.sending = false;
    $scope.update = function() {
        if ($scope.formData.isValid) {
            RestrictedUser.update({}, $scope.formData, function() {
                Notification.success(translateFilter('profil.success'));
                $state.go('app.dashboard', {}, {reload: true});
            }, function() {
                $scope.sendSuccess = false;
                $scope.sendError = true;
                $scope.sending = false;
            });
        }
    };


    /**
     * remove selected img, then current img, then social pimg
     * @return {[type]} [description]
     */
    $scope.removeImage = function() {
        if ($scope.files && $scope.files.length) {
            $scope.files = [];
        } else if ($scope.formData.imageProfilKey) {
            $scope.formData.imageProfilKey = null;
        } else if ($scope.formData.socialProfilImageUrl) {
            $scope.formData.socialProfilImageUrl = null;
        }
    };

    $scope.verify = false;
    $scope.doVerify = function() {
        $scope.verify = true;
        if ($scope.formData.isValid) {
            $scope.sendError = false;
            $scope.sendSuccess = false;
            $scope.sending = true;
            $scope.update();
        }
    };
});
