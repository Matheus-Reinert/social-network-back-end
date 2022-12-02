CREATE table `users` (
	`id` int8 auto_increment not null primary key,
    `name` varchar(100),
    `lastName` varchar(100),
    `email` varchar(100) not null,
    `subtitle` varchar(100),
    `aboutMe` varchar(100),
    `username` varchar(100) not null,
    `password` varchar(100) not null,	
    `token` varchar(100) not null
    );	
    
    CREATE TABLE `followers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `follower_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
); 

CREATE TABLE `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_text` varchar(150) NOT NULL,
  `dateTime` timestamp NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `likes` double NOT NULL,
  PRIMARY KEY (`id`)
); 

CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment` varchar(150) NOT NULL,
  `commentParent_id` bigint(20),
  `dateTime` timestamp NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `post_id` bigint(20),
  `likes` double NOT NULL,
  PRIMARY KEY (`id`)
);

