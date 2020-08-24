package idv.fd.purchase.api;

import idv.fd.error.AppError;
import idv.fd.purchase.dto.*;
import idv.fd.purchase.model.PurchaseHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Purchase", description = "The Purchase APIs")
public interface PurchaseApi {

    @Operation(summary = "Purchase a dish from a restaurant", tags = {"Purchase"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PurchaseHistory.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/purchases")
    PurchaseHistory purchaseDish(@Valid @RequestBody Purchase purchase);


    @Operation(summary = "Find top x users by total transaction amount within a date range", tags = {"Purchase"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserTxAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Parameter(name = "top", description = "The specified top x")
    @Parameter(name = "fromDate", description = "The specified start date")
    @Parameter(name = "toDate", description = "The specified end date")
    @GetMapping(value = "/transactions/top-users")
    List<UserTxAmount> findTopTxUsers(
            @RequestParam(name = "top", required = false, defaultValue = "10") @Min(1) @Max(100) int top,
            @RequestParam("fromDate") LocalDate from,
            @RequestParam("toDate") LocalDate to);


    @Operation(summary = "Find total number and dollar value of transactions that happened within a date range", tags = {"Purchase"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TxNumbAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Parameter(name = "fromDate", description = "The specified start date")
    @Parameter(name = "toDate", description = "The specified end date")
    @GetMapping(value = "/transactions/sum")
    List<TxNumbAmount> findTxNumbAmount(
            @RequestParam("fromDate") LocalDate from,
            @RequestParam("toDate") LocalDate to);


    @Operation(summary = "Find most popular restaurants by transaction volume, either by number of transactions or transaction dollar value", tags = {"Purchase"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantTxAmount.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Parameter(name = "byAmount", description = "To indicate it's by transaction amount or number of transactions")
    @GetMapping(value = "/transactions/max-restaurants")
    List<RestaurantTxAmount> findMaxTxRestaurants(
            @RequestParam(name = "byAmount", required = false, defaultValue = "false") boolean byAmount);


    @Operation(summary = "Find total number of users who made transactions above or below $v within a date range", tags = {"Purchase"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Count.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AppError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Parameter(name = "amount", description = "The specified transaction amount")
    @Parameter(name = "fromDate", description = "The specified start date")
    @Parameter(name = "toDate", description = "The specified end date")
    @Parameter(name = "lessThan", description = "To indicate less or more than the specified transaction amount")
    @GetMapping(value = "/transactions/user-count")
    Count getUserCount(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("fromDate") LocalDate from,
            @RequestParam("toDate") LocalDate to,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan);
}
