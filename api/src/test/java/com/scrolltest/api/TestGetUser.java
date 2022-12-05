package com.scrolltest.api;

import static io.restassured.RestAssured.given;
import org.testng.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.siyaAPI.constants.Endpoints;
import com.siyaAPI.constants.Path;
import com.siyaAPI.util.RestUtilities;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.*;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.testng.Assert.assertTrue;

public class TestGetUser {

	 private static final String EMAIL_PATTERN = "^(.+)@(\\S+)$";

	    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	public static RequestSpecBuilder REQUEST_BUILDER = new RequestSpecBuilder();

	RequestSpecification reqSpec;
	ResponseSpecification resSpec;
	private int userId;
	

	@BeforeClass
	public void setup() {
		reqSpec = RestUtilities.getRequestSpecification();
		reqSpec.queryParam("username", "Delphine");
		

		resSpec = RestUtilities.getResponseSpecification();
	}

	@Test
	public void getUsers()

	{
		
		given().spec(RestUtilities.createQueryParam(reqSpec, "count", "1")).when().get(Endpoints.GET_ALL_USERS).then()
				.log().all().spec(resSpec).body("username", hasItem("Delphine")).
		        assertThat().
		        statusCode(200);
	}

	@Test
	public void getPostsOfAUser()

	{
	
		 userId = getIdOfUser("Delphine");
		//System.out.println(userId);
		reqSpec.queryParam("userId", userId);
		given().spec(RestUtilities.createQueryParam(reqSpec, "count", "1")).when().get(Endpoints.GET_ALL_POSTS).then()
		.assertThat().statusCode(200)
				.log().all().spec(resSpec)
				.assertThat().
		        statusCode(200);
	}

	@Test
	public void getCommentsOfPost()

	{
		RestUtilities.setEndPoint(Endpoints.GET_ALL_POSTS);
		Response res = RestUtilities.getResponse(RestUtilities.createQueryParam(reqSpec, "count", "1"), "GET");

		ArrayList<Integer> idList = res.path("id");
		// reqSpec.queryParam("userId", id);
		//userId = getIdOfUser("Delphine");
		
		Response commentResponse;
		for(Integer i : idList)
		{
			reqSpec.queryParam("postId", i);
			given()
					.spec(RestUtilities.createQueryParam(reqSpec, "count", "1"))
					.when().get(Endpoints.GET_ALL_COMMENTS).then()
					.log().all()
					.spec(resSpec).assertThat().
			        statusCode(200);
			RestUtilities.setEndPoint(Endpoints.GET_ALL_COMMENTS);
			commentResponse = RestUtilities.getResponse(RestUtilities.createQueryParam(reqSpec, "count", "1"), "GET");
			ArrayList<String> commentsList = commentResponse.path("email");
			//System.out.println(commentsList.get(i));
			assertTrue(isValid(commentsList.get(i)));
			
			
		}
		
	}
	
	private int getIdOfUser(String user)
	{
		reqSpec.queryParam("username", user);
		RestUtilities.setEndPoint(Endpoints.GET_ALL_USERS);
		Response res = RestUtilities.getResponse(RestUtilities.createQueryParam(reqSpec, "count", "1"), "GET");

		ArrayList<Integer> idList = res.path("id");
		int id = idList.get(0);
		return id;
	}
	
		
	public static boolean isValid(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
