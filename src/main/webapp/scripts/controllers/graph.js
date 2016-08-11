'use strict';

angular
		.module('sbAdminApp')
		.controller(
				'GraphCtrl',
				function($scope, $interval, LayoutService) {

					var svgWidth = $('#svg_graphmap').width();
					var svgHeight = $('#svg_graphmap').height();

					$scope.generateRandomNumber = function(startNum, endNum) {
						return startNum
								+ Math.floor(Math.random()
										* (endNum - startNum));
					};

					$scope.generateId = function(prefix) {
						return new Date().getTime() + ''
								+ (10 + Math.floor(Math.random() * 89));
					};

					$scope.selectedNodes = [];
					$scope.nodeImages = [];

					$scope.defaultNode = {
						width : 36,
						height : 36,
						// label
						label : 'Node',
						labelSize : 12,
						labelFill : 'black',
						// choice to either shape or image
						// shape
						shape : 'ellipse',
						// color
						fill : 'red',
						fillOpacity : 0.7,
						// border
						stroke : 'black',
						strokeOpacity : 0.9,
						strokeWidth : 1,
						// image
						image : undefined
					};

					$scope.defaultLink = {
						stroke : 'black',
						strokeOpacity : 0.9,
						strokeWidth : 2,
					};

					$scope.data = {
						nodes : [],
						links : []
					};

					$scope.getNode = function(id) {
						for ( var i in $scope.data.nodes)
							if ($scope.data.nodes[i].id == id)
								return $scope.data.nodes[i];
					};

					$scope.getLink = function(id) {
						for ( var i in $scope.data.links)
							if ($scope.data.links[i].id == id)
								return $scope.data.links[i];
					};

					$scope.getRelatedLinks = function(node) {
						var relatedLinks = [];
						for ( var i in $scope.data.links) {
							if ($scope.data.links[i].source == node.id)
								relatedLinks.push($scope.data.links[i]);
							if ($scope.data.links[i].target == node.id)
								relatedLinks.push($scope.data.links[i]);
						}
						return relatedLinks;
					};

					$scope.calculateLinkPoints = function(node, ux, uy) {
						if (node.shape == 'ellipse') {
							var halfWidth = node.width / 2.0;
							var halfHeight = node.height / 2.0;

							var squareHW = halfWidth * halfWidth;
							var squareHH = halfHeight * halfHeight;

							var dx = Math.sqrt(1 / (1 / squareHW + uy * uy
									/ (ux * ux * squareHH)));
							if (ux == 0)
								dx = 0;

							var dy = Math.sqrt(squareHH
									* (1 - dx * dx / squareHW));
							if (ux < 0)
								dx = -dx;
							if (uy < 0)
								dy = -dy;

							return {
								x : node.x + dx,
								y : node.y + dy
							};
						} else {
							var linkTangent = ux != 0 ? Math.abs(uy / ux)
									: Number.MAX_VALUE;
							var nodeTangent = node.height / (node.width * 1.0);
							// 남북에 닿는지 동서에 닿는지 체크한다. (남북에 닿으면 true)
							var flagNorthSouth = linkTangent >= nodeTangent;

							var retX;
							var retY;
							if (flagNorthSouth) {
								if (uy > 0)
									retY = node.y + node.height / 2.0; // 북쪽
								else
									retY = node.y - node.height / 2.0; // 남쪽
								retX = node.x + ux / Math.abs(uy) * node.height
										/ 2.0;
							} else {
								if (ux > 0)
									retX = node.x + node.width / 2.0; // 동쪽
								else
									retX = node.x - node.width / 2.0; // 서쪽
								retY = node.y + uy / Math.abs(ux) * node.width
										/ 2.0;
							}
							return {
								x : retX,
								y : retY
							};
						}
					}

					$scope.updateLinkPoints = function(link) {
						var sourceNode = $scope.getNode(link.source);
						var targetNode = $scope.getNode(link.target);

						var sourcePoints = $scope.calculateLinkPoints(
								sourceNode, targetNode.x - sourceNode.x,
								targetNode.y - sourceNode.y);
						link.x1 = sourcePoints.x;
						link.y1 = sourcePoints.y;

						var targetPoints = $scope.calculateLinkPoints(
								targetNode, sourceNode.x - targetNode.x,
								sourceNode.y - targetNode.y);
						link.x2 = targetPoints.x;
						link.y2 = targetPoints.y;
					}

					$scope.initNode = function(node) {
						// image url to id processing
						if (node.image) {
							var index = $scope.nodeImages.indexOf(node.image);
							if (index >= 0) {
								node.imageid = index;
							} else {
								node.imageid = $scope.nodeImages.length;
								$scope.nodeImages.push(node.image);
							}
						}
						// inject default node values
						for ( var key in $scope.defaultNode)
							if (!node[key])
								node[key] = $scope.defaultNode[key];
						// set node position
						node.x = $scope.generateRandomNumber(100,
								svgWidth - 100);
						node.y = $scope.generateRandomNumber(100,
								svgHeight - 100);
					};

					$scope.initLink = function(link) {
						// inject default link values
						for ( var key in $scope.defaultLink)
							if (!link[key])
								link[key] = $scope.defaultLink[key];
						// set link position
						$scope.updateLinkPoints(link);
					};

					$scope.initTest = function() {
						for (var i = 0; i < 10; i++) {
							var node = angular.copy($scope.defaultNode);
							node.id = $scope.generateId('N');
							node.label = 'Node' + (i + 1);
							var nodeImgRN = $scope.generateRandomNumber(0, 4);
							if (nodeImgRN == 0) {
								node.image = 'images/virus-41379_1280.png';
							} else if (nodeImgRN == 1) {
								node.image = 'images/Paper-icon.png';
							} else if (nodeImgRN == 2) {
								node.image = 'images/Network-Domain-icon.png';
							} else if (nodeImgRN == 3) {
								node.image = 'images/people3.png';
							}
							$scope.data.nodes.push(node);
						}
						for ( var index in $scope.data.nodes) {
							for (var i = 0; i < $scope.generateRandomNumber(1,
									5); i++) {
								var link = angular.copy($scope.defaultLink);
								link.id = $scope.generateId('L');
								link.source = $scope.data.nodes[index].id;
								link.target = $scope.data.nodes[$scope
										.generateRandomNumber(0,
												$scope.data.nodes.length)].id;
								$scope.data.links.push(link);
							}
						}
					};
					$scope.initTest();

					$scope.init = function() {
						// nodes
						for ( var i in $scope.data.nodes)
							$scope.initNode($scope.data.nodes[i]);
						// links
						for ( var i in $scope.data.links)
							$scope.initLink($scope.data.links[i]);
					};
					$scope.init();

					$scope.svgMouseDown = function(event) {
						$scope.drag = true;
						$scope.dragOffsetX = event.offsetX;
						$scope.dragOffsetY = event.offsetY;
						$scope.dragWidth = 1;
						$scope.dragHeight = 1;
					};

					$scope.svgMouseMove = function(event) {
						$scope.nodeMouseMove(event);
						$scope.resizeMouseMove(event);

						if (!$scope.drag)
							return;
						$scope.dragX = Math.min(event.offsetX,
								$scope.dragOffsetX);
						$scope.dragY = Math.min(event.offsetY,
								$scope.dragOffsetY);
						$scope.dragWidth = Math.abs(event.offsetX
								- $scope.dragOffsetX);
						$scope.dragHeight = Math.abs(event.offsetY
								- $scope.dragOffsetY);
					};

					$scope.svgMouseUp = function(event) {
						$scope.nodeMouseUp();
						$scope.resizeMouseUp();

						$scope.drag = false;

						if (!$scope.isNodeMouseDown
								&& !$scope.isResizeMouseDown && !event.shiftKey)
							$scope.data.nodeSelectionClear();
						$scope.isNodeMouseDown = false;
						$scope.isResizeMouseDown = false;
					};

					$scope.nodeMouseDown = function(event, node) {
						$scope.isNodeMouseDown = true;

						if (!event.shiftKey)
							$scope.data.nodeSelectionClear();
						node.selected = true;
						$scope.selectedNodes.push(node);

						$scope.dragNode = node;
						$scope.dragNodeOffsetX = event.offsetX;
						$scope.dragNodeOffsetY = event.offsetY;
						$scope.dragNodeX = node.x;
						$scope.dragNodeY = node.y;
						$scope.dragRelatedLinks = $scope
								.getRelatedLinks($scope.dragNode);
					};

					$scope.nodeMouseMove = function(event) {
						if (!$scope.dragNode)
							return;
						$scope.dragNode.x = $scope.dragNodeX + event.offsetX
								- $scope.dragNodeOffsetX;
						$scope.dragNode.y = $scope.dragNodeY + event.offsetY
								- $scope.dragNodeOffsetY;
						for ( var i in $scope.dragRelatedLinks)
							$scope.updateLinkPoints($scope.dragRelatedLinks[i]);

						$scope.drag = false;
					};

					$scope.nodeMouseUp = function(event) {
						$scope.dragNode = undefined;
					};

					$scope.data.nodeSelectionClear = function() {
						for ( var i in $scope.selectedNodes) {
							$scope.selectedNodes[i].selected = false;
						}
						$scope.selectedNodes = [];
					}

					$scope.resizeMouseDown = function(event, node, direct) {
						$scope.isResizeMouseDown = true;

						$scope.resizeNode = node;
						$scope.resizeDirect = direct;
						$scope.resizeOffsetX = event.offsetX;
						$scope.resizeOffsetY = event.offsetY;
						$scope.resizeWidth = node.width;
						$scope.resizeHeight = node.height;
					};

					$scope.resizeMouseMove = function(event) {
						if (!$scope.resizeNode)
							return;
						if ($scope.resizeDirect.includes('w'))
							$scope.resizeNode.width = Math
									.max(
											1,
											$scope.resizeWidth
													+ (event.offsetX - $scope.resizeOffsetX)
													* -2);
						else if ($scope.resizeDirect.includes('e'))
							$scope.resizeNode.width = Math
									.max(
											1,
											$scope.resizeWidth
													+ (event.offsetX - $scope.resizeOffsetX)
													* 2);
						if ($scope.resizeDirect.includes('n'))
							$scope.resizeNode.height = Math
									.max(
											1,
											$scope.resizeHeight
													+ (event.offsetY - $scope.resizeOffsetY)
													* -2);
						else if ($scope.resizeDirect.includes('s'))
							$scope.resizeNode.height = Math
									.max(
											1,
											$scope.resizeHeight
													+ (event.offsetY - $scope.resizeOffsetY)
													* 2);
						
						$scope.getRelatedLinks($scope.resizeNode).forEach(function(link) {
							$scope.updateLinkPoints(link);
						});
						
						$scope.drag = false;
					};

					$scope.resizeMouseUp = function(event) {
						$scope.resizeNode = undefined;
					};
					
					$scope.runLayout = function() {
						var sources = [];
						var targets = [];
						var xs = [];
						var ys = [];
						
						var nodes = [];
						for ( var i in $scope.data.nodes) {
							nodes.push($scope.data.nodes[i].id);
						}
						
						for ( var i in $scope.data.links) {
							var link = $scope.data.links[i];
							sources.push(link.source);
							targets.push(link.target);
							
							xs.push($scope.getNode(link.source).x);
							ys.push($scope.getNode(link.source).y);
							
							nodes.pop(link.source);
							nodes.pop(link.target);
						}
						
						for ( var i in nodes) {
							sources.push(nodes[i]);
							targets.push(nodes[i]);
							
							xs.push($scope.getNode(nodes[i]).x);
							ys.push($scope.getNode(nodes[i]).y);
						}
						
						LayoutService.doLayout({sources: sources, targets: targets, xs: xs, ys: ys, width: svgWidth, height: svgHeight}).then(function(data) {
							var iteration = 100;
							var ratio = 0.2;
							var delay = 20;
							setTimeout(function callback(){
								// 좌표들을 계속 이동
								data.nodes.forEach(function(nodeId, i) {
									var node = $scope.getNode(nodeId);
									var dx = data.xs[i]-node.x;
									var dy = data.ys[i]-node.y;
									
									$scope.$apply(function() {
										node.x += Math.floor(dx*ratio);
										node.y += Math.floor(dy*ratio);
										$scope.getRelatedLinks({id:nodeId}).forEach(function(link) {
											$scope.updateLinkPoints(link);
										});
									});
								});
								iteration--;
								if (iteration>0) setTimeout(callback, delay);
							}, delay);
							
						}, function(errResponse) {
							console.error('Layout Response Error');
						});
					};
					
					$scope.nodeDoubleClick = function(node) {
						alert('Node Double Click : ' + node.id);
				    };
				    
				    $scope.linkClick = function(link) {
				    	// alert('Link Click : ' + link.id);
				    };
				    
				});
