package idv.fd.restaurant.api;


import idv.fd.error.AppError;
import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Tag(name = "Restaurant", description = "The Restaurant APIs")
public interface RestaurantApi {

    @Operation(summary = "Find restaurants by pagination", tags = {"Restaurant"})
    @Parameter(name = "page", description = "The specified page")
    @Parameter(name = "size", description = "The number of records in the page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurant.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/restaurants")
    Page<Restaurant> findRestaurants(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size);


    @Operation(summary = "Edit restaurant name", tags = {"Restaurant"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Restaurant.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PutMapping("/restaurants")
    Restaurant updateRestaurant(@Valid @RequestBody EditRestaurant editRest);


    @Operation(summary = "Search restaurants by name", tags = {"Restaurant"})
    @Parameter(name = "name", description = "The specified restaurant name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/restaurants/findByName", params = "name")
    List<RestaurantInfo> findRestaurantsByName(
            @RequestParam(name = "name") String name);


    @Operation(summary = "List all restaurants that are open at a certain time on a day of the week", tags = {"Restaurant"})
    @Parameter(name = "time", description = "The specified open time")
    @Parameter(name = "dayOfWeek", description = "The day of week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/restaurants/findByOpenTime", params = "time")
    List<RestaurantInfo> findRestaurantsByTime(
            @RequestParam(name = "time") String timeStr,
            @RequestParam(name = "dayOfWeek", required = false) @Min(0) @Max(6) Integer dayOfWeek);


    @Operation(summary = "List all restaurants that are open for more or less than x hours per day or week", tags = {"Restaurant"})
    @Parameter(name = "openHours", description = "The specified open hours")
    @Parameter(name = "lessThan", description = "To indicate less or more than the specified open hours")
    @Parameter(name = "perWeek", description = "To indicate per week or per day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/restaurants/findByOpenHours", params = "openHours")
    List<? extends RestaurantInfo> findRestaurantsByOpenPeriod(
            @RequestParam(name = "openHours") @Min(1) @Max(24) int openHours,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan,
            @RequestParam(name = "perWeek", required = false, defaultValue = "false") boolean perWeek);


    @Operation(summary = "List all restaurants that have more or less than x number of dishes within a price range", tags = {"Restaurant"})
    @Parameter(name = "dishNumb", description = "The specified dish number")
    @Parameter(name = "lessThan", description = "To indicate less or more than the specified dish number")
    @Parameter(name = "maxPrice", description = "Max price of dish")
    @Parameter(name = "minPrice", description = "Min price of dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantInfo.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "restaurants/findByDishNumb", params = "dishNumb")
    List<? extends RestaurantInfo> findRestaurantsByDishNumb(
            @RequestParam(name = "dishNumb") @Min(1) @Max(1000) int dishNumb,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "minPrice", required = false) Double minPrice);
}
