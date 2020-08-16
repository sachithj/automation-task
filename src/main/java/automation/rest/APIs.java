package automation.rest;

import automation.common.LoggerHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class APIs {
    private static final Logger logger = LoggerHelper.getLogger(APIs.class);

    public APIs(String host) {
        RestAssured.baseURI = host;

    }

    Response response;

    // checkAvailability API
    public Response checkAvailabilityApi(String date) {
        RequestSpecification httpRequest = RestAssured.given();
        logger.info("Calling CheckAvailability API with Date parameter {" + date + "}");
        httpRequest.when().log().all();

        response = httpRequest.get("/checkAvailability/{date}", date);
        logger.info("CheckAvailability API Response: ");
        response.then().log().all();
        return response;

    }

    public Response bookRoomApi(int numOfDays, String checkInDate) {
        RequestSpecification httpRequest = RestAssured.given();

        JSONObject requestJSON = new JSONObject();
        requestJSON.put("numOfDays", numOfDays);
        requestJSON.put("checkInDate", checkInDate);
        logger.info("Calling BookRoom API with request JSON: " + requestJSON.toString());

        httpRequest.request().body(requestJSON.toString());
        httpRequest.when().log().all();

        response = httpRequest.post("/bookRoom");
        logger.info("BookRoom API Response: ");
        response.then().log().all();
        return response;
    }

}
