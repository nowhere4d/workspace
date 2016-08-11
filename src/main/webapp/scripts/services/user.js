'use strict';

angular.module('sbAdminApp')
	.factory('UserService', [
		'$http',
		'$q',
		function($http, $q) {

			var baseUrl = location.href.substring(0, location.href.indexOf('/', location.href.indexOf(location.host) + location.host.length + 1)).replace('\/#', '');

			return {

				fetchAllUsers : function() {
					return $http.get(baseUrl + '/user/').then(
							function(response) {
								return response.data;
							}, function(errResponse) {
								console.error('Error while fetching users');
								return $q.reject(errResponse);
							});
				},

				createUser : function(user) {
					return $http.post(baseUrl + '/user/', user).then(
							function(response) {
								return response.data;
							}, function(errResponse) {
								console.error('Error while creating user');
								return $q.reject(errResponse);
							});
				},

				updateUser : function(user, id) {
					return $http.put(baseUrl + '/user/' + id, user).then(
							function(response) {
								return response.data;
							}, function(errResponse) {
								console.error('Error while updating user');
								return $q.reject(errResponse);
							});
				},

				deleteUser : function(id) {
					return $http['delete'](baseUrl + '/user/' + id).then(
							function(response) {
								return response.data;
							}, function(errResponse) {
								console.error('Error while deleting user');
								return $q.reject(errResponse);
							});
				}
			};
		} ]);
