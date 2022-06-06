package com.ringme.camid.Notification.repos.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.ringme.camid.Notification.repos.mongodb.entity.CamId_MessageInfo;
import com.ringme.camid.Notification.repos.mongodb.entity.Camid_SurveyMessage;
import com.ringme.camid.Notification.repos.mongodb.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MongoDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final Logger logger = LogManager.getLogger(MongoDao.class);

    public void saveMessageInfo(CamId_MessageInfo messageInfo) {
        try {
            mongoTemplate.save(messageInfo, "message_info");
        } catch (Exception e) {
            logger.error("saveMessageInfo|Exception|" + e.getMessage(), e);
        }

    }

    public void saveMessageSurvey(Camid_SurveyMessage messageInfo) {
        try {
            mongoTemplate.save(messageInfo, "message_survey");
        } catch (Exception e) {
            logger.error("saveMessageSurvey|Exception|" + e.getMessage(), e);
        }
    }

    public void increaseSurvey(String id, int num, String type) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        try {
            switch (type) {
                case "satisfied":
                    mongoTemplate.updateFirst(query, new Update().inc("satisfied", num), "message_survey");
                    break;
                case "normal":
                    mongoTemplate.updateFirst(query, new Update().inc("normal", num), "message_survey");
                    break;
                case "unsatisfied":
                    mongoTemplate.updateFirst(query, new Update().inc("unsatisfied", num), "message_survey");
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("increaseSurvey|Exception|" + e.getMessage(), e);
        }
    }

    public User getRegID(String msisdn) {
        User user = new User();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(msisdn));
            query.addCriteria(Criteria.where("regid").exists(true));
            user = mongoTemplate.findOne(query, User.class, "users");
        } catch (Exception e) {
            logger.error("getRegID|Exception|" + e.getMessage(), e);
        }
        return user;
    }

    public List<CamId_MessageInfo> getCampaign(String msisdn, int limit, int skip) {
        Query query = new Query(Criteria.where("msisdn").is(msisdn));
        List<CamId_MessageInfo> list = new ArrayList<>();
        try {
            list = mongoTemplate.find(query.limit(limit).skip(skip * limit)
                    .with(Sort.by(Sort.Direction.DESC, "notified_date")), CamId_MessageInfo.class, "message_info");
        } catch (Exception e) {
            logger.error("getCampaign|Exception|" + e.getMessage(), e);
        }
        return list;
    }

    public List<Camid_SurveyMessage> getSurvey(String msisdn, int limit, int skip) {
        Query query = new Query(Criteria.where("msisdn").is(msisdn));
        List<Camid_SurveyMessage> list = new ArrayList<>();
        try {
            list = mongoTemplate.find(query.limit(limit).skip(skip * limit)
                    .with(Sort.by(Sort.Direction.DESC, "notified_date")), Camid_SurveyMessage.class, "message_survey");
        } catch (Exception e) {
            logger.error("getSurvey|Exception|" + e.getMessage(), e);
        }
        return list;
    }

    public void deleteMessageInfo(String msId) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(msId)));
        try {
            mongoTemplate.remove(query, "message_info");
        } catch (Exception e) {
            logger.error("deleteMessageInfo|Exception|" + e.getMessage(), e);
        }
    }

    public long fillSeenCampaign(String msisdn) {
        long result = -1;
        Query query = new Query(Criteria.where("msisdn").is(msisdn).and("status").is(0));
        try {
//            result = mongoTemplate.count(query, CamId_MessageInfo.class);
            UpdateResult updateResult = mongoTemplate.updateMulti(query, new Update().set("status", 1), CamId_MessageInfo.class);
            result = updateResult.getModifiedCount();
        } catch (Exception e) {
            logger.error("fillSeenCampaign|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    public void updateCampaign(String msId) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(msId)).and("status").is(0));
        try {
            mongoTemplate.updateFirst(query, new Update().set("status", 1), "message_info");

        } catch (
                Exception e) {
            logger.error("updateCampaign|Exception|" + e.getMessage(), e);
        }
    }

    public long deleteMessageInfoAll(String msisdn) {
        Query query = new Query(Criteria.where("msisdn").is(msisdn));
        long result = 0L;
        try {
            result = mongoTemplate.remove(query, "message_info").getDeletedCount();
        } catch (Exception e) {
            logger.error("deleteMessageInfoAll|Exception|" + e.getMessage(), e);
        }
        return result;
    }
}
