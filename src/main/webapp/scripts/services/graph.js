'use strict';

angular
		.module('sbAdminApp')
		.factory(
				'LayoutService',
				[
						'$http',
						'$q',
						function($http, $q) {

							var baseUrl = location.href.substring(
									0,
									location.href.indexOf('/', location.href
											.indexOf(location.host)
											+ location.host.length + 1))
									.replace('\/#', '');

							return {

								doLayout : function(data) {
									return $http
											.post(baseUrl + '/layout/', data)
											.then(
													function(response) {
														return response.data;
													},
													function(errResponse) {
														console
																.error('Error while fetching layout');
														return $q
																.reject(errResponse);
													});
								}
							};
						} ]);
