/**
 * Model for revisions
 */

var mongoose = require('mongoose');

var RevisionSchema = new mongoose.Schema(
		{
			title: String,
			timestamp: Date,
			user: String,
			userType: String
		},
		{
		 	versionKey: false
		});

// create index - this will be created in the step of data pre-processing
// format: mySchema.index({field1: 1, field2: 1}, {unique: true});
// RevisionSchema.index({timestamp: 1});
// RevisionSchema.index({timestamp: -1});
// RevisionSchema.index({user: 1});
// RevisionSchema.index({userType: 1});

/*
 * Overall feature
 */
// this is to find the given number of articles with the highest revisions
// {'$limit': num}
RevisionSchema.statics.findArticleHighestRev = function(callback)
{
    Revision.aggregate([
        {'$group': {'_id':"$title", 'totalRev': {$sum: 1}}},
        {'$sort': {"totalRev": -1}}
	])
		.exec(function (error, result)
		{
			if(error)
                return callback(error);
			else if(!result)
			{
                var err = new Error('Result not found!');
				err.status = 401;
				return callback(err);
			}
			else
			{
				console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
		});
};
// this is to find the given number of articles with the lowest revisions
// {'$limit': num}
RevisionSchema.statics.findArticleLowestRev = function(callback)
{
    Revision.aggregate([
        {'$group': {'_id':"$title", 'totalRev': {$sum: 1}}},
        {'$sort': {"totalRev": 1}},
        {"$limit": 3}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the article edited by the largest group of registered users
RevisionSchema.statics.findArticleLargeRegUserEdit = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: "regular"}},
        {"$group": {_id: "$title", users: {$addToSet: "$user"}}},
        {"$unwind": "$users"},
        {"$group": {_id: "$_id", userCount: {$sum: 1}}},
        {"$sort": {"userCount": -1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the article edited by the smallest group of registered users
RevisionSchema.statics.findArticleSmallRegUserEdit = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: "regular"}},
        {"$group": {_id: "$title", users: {$addToSet: "$user"}}},
        {"$unwind": "$users"},
        {"$group": {_id: "$_id", userCount: {$sum: 1}}},
        {"$sort": {"userCount": 1}},
        {"$limit": 1}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the top 3 articles with the longest history (age)
RevisionSchema.statics.findArticleLongHistory = function(callback)
{
    Revision.aggregate([
        //{"$sort": {"timestamp": 1}},
        //{"$limit": 5},  // if fixed data set then can make data smaller
        {"$group": {_id: "$title", timestamp: {"$min": "$timestamp"}}},
        {"$sort": {"timestamp": 1}},
        {"$limit": 3}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the top 3 articles with the shortest history(age)
RevisionSchema.statics.findArticleShortHistory = function(callback)
{
    Revision.aggregate([
        //{"$sort": {"timestamp": -1}},
        //{"$limit": 5},  // if fixed data set then can make data smaller
        {"$group": {_id: "$title", timestamp: {"$min": "$timestamp"}}},
        {"$sort": {"timestamp": -1}},
        {"$limit": 3}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the number of revisions by year of admin users
RevisionSchema.statics.findAdminRevByYear = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: {"$in": ["admin", "adminbot"]}}},
        {"$group": {_id: {"$year": "$timestamp"}, countRev: {"$sum": 1}}},
        {"$sort": {"_id": 1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the number of revisions by year of bot users
RevisionSchema.statics.findBotRevByYear = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: "bot"}},
        {"$group": {_id: {"$year": "$timestamp"}, countRev: {"$sum": 1}}},
        {"$sort": {"_id": 1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the number of revisions by year of regular users
RevisionSchema.statics.findRegularRevByYear = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: "regular"}},
        {"$group": {_id: {"$year": "$timestamp"}, countRev: {"$sum": 1}}},
        {"$sort": {"_id": 1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the number of revisions by year of anon users
RevisionSchema.statics.findAnonRevByYear = function(callback)
{
    Revision.aggregate([
        {"$match": {userType: "anon"}},
        {"$group": {_id: {"$year": "$timestamp"}, countRev: {"$sum": 1}}},
        {"$sort": {"_id": 1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Overall");
                //console.log(result);
                return callback(null, result);
            }
        });
};

/*
 * Author feature
 */
// Find the particular author by the given author name from the end user
RevisionSchema.statics.findAuthor = function(name, callback)
{
    Revision.aggregate([
        {"$match": {user: {"$regex": name, "$options": 'i'}}},
        {"$group": {_id: {article: "$title", author: "$user"}, revisionNum: {"$sum": 1}, time: {"$push": "$timestamp"}}},
        {"$project": {title: "$_id.article", author: "$_id.author", revisionNum: "$revisionNum", time: "$time"}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result: Author");
                //console.log(result);
                return callback(null, result);
            }
        });
};

/*
 * Individual feature
 */
// this is to find all articles with their total number of revisions and latest timestamp
RevisionSchema.statics.findAllTitlesRev = function(callback)
{
    Revision.aggregate([
        {'$group': {'_id':"$title", 'totalRev': {$sum: 1}, latestTime:{$max:"$timestamp"}}},
        {'$sort': {"totalRev": -1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");

                return callback(null, result);
            }
        });
};

// this is to find a particular article's total number of revisions and latest timestamp
RevisionSchema.statics.findArticleRev = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$group:{_id:"$title",total:{$sum:1},latestTime:{$max:"$timestamp"}}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to find the top 5 regular users ranked by total revision numbers on a particular article and the respective revision numbers
RevisionSchema.statics.findTopUsers = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"regular"}},
        {$group:{_id:"$user",total:{$sum:1}}},
        {$sort:{"total":-1}},
        {$limit:5}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The findTopUsers: query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to count revision number of one or a few of the top users for a particular article by year, the parameter users should be an array
RevisionSchema.statics.findTopUsersByYear = function(article,users, callback)
{
    if(users!="undefined"){
        Revision.aggregate([
            {$match:{title:article}},
            {$match:{userType:"regular"}},
            {$match:{user:users}},
            {$project:{title:1,user:1,year:{$year:"$timestamp"}}},
            {$group:{_id:"$year",total:{$sum:1}}},
            {$sort:{_id:1}}
        ])
            .exec(function (error, result)
            {
                if(error)
                    return callback(error);
                else if(!result)
                {
                    var err = new Error('Result not found!');
                    err.status = 401;
                    return callback(err);
                }
                else
                {
                    console.log("The query result:");
                    console.log(result);
                    return callback(null, result);
                }
            });
    }else{
        return callback(null,{_id:2018,total:0})
    }



};

// this is to count the revision number of regular users for a particular article(suggest that not using this but using total number subtract other 3 types users)
RevisionSchema.statics.findRegulars = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"regular"}},
        {$count:"x"}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};


// this is to count the revision number of regular users by year for a particular article
RevisionSchema.statics.findRegularByYear = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"regular"}},
        {$project:{title:1,user:1,year:{$year:"$timestamp"}}},
        {$group:{_id:"$year",total:{$sum:1}}},
        {$sort:{_id:1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to count the revision number of admins for a particular article
RevisionSchema.statics.findArticleAdminRev = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {"$match": {userType: {"$in": ["admin", "adminbot"]}}},
        {$count:"x"}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to count the revision number of admins by year for a particular article
RevisionSchema.statics.findArticleAdminRevByYear = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {"$match": {userType: {"$in": ["admin", "adminbot"]}}},
        {$project:{title:1,user:1,year:{$year:"$timestamp"}}},
        {$group:{_id:"$year",total:{$sum:1}}},
        {$sort:{_id:1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};
// this is to count the revision number of bots for a particular article
RevisionSchema.statics.findArticleBotRev = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"bot"}},
        {$count:"x"}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to count the revision number of bots by year for a particular article
RevisionSchema.statics.findArticleBotRevByYear = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"bot"}},
        {$project:{title:1,user:1,year:{$year:"$timestamp"}}},
        {$group:{_id:"$year",total:{$sum:1}}},
        {$sort:{_id:1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};
// this is to count the revision number of anons for a particular article
RevisionSchema.statics.findArticleAnonRev = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"anon"}},
        {$count:"x"}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

// this is to count the revision number of anons by year for a particular article
RevisionSchema.statics.findArticleAnonRevByYear = function(article, callback)
{
    Revision.aggregate([
        {$match:{title:article}},
        {$match:{userType:"anon"}},
        {$project:{title:1,user:1,year:{$year:"$timestamp"}}},
        {$group:{_id:"$year",total:{$sum:1}}},
        {$sort:{_id:1}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

RevisionSchema.statics.getAllAdmins = function(callback)
{
    Revision.aggregate([
        {$match:{userType:{$in:["admin","adminbot"]}}},
        {$group:{_id:"$user"}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                // console.log("The query result:");
                // console.log(result);
                return callback(null, result);
            }
        });
};

RevisionSchema.statics.getAllBots = function(callback)
{
    Revision.aggregate([
        {$match:{userType:"bot"}},
        {$group:{_id:"$user"}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                // console.log("The query result:");
                // console.log(result);
                return callback(null, result);
            }
        });
};

RevisionSchema.statics.getMinMaxForArticle = function(article,callback)
{
    Revision﻿.aggregate([
        {$match:{title:article}},
        {$project:{year:{$year:"$timestamp"}}},
        {$group:{_id:"year",min:{$min:"$year"},max:{$max:"$year"}}}
    ])
        .exec(function (error, result)
        {
            if(error)
                return callback(error);
            else if(!result)
            {
                var err = new Error('Result not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                console.log("The query result:");
                console.log(result);
                return callback(null, result);
            }
        });
};

var Revision = mongoose.model('Revision', RevisionSchema, 'revisions');

module.exports = Revision;