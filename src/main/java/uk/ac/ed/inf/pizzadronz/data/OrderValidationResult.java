package uk.ac.ed.inf.pizzadronz.data;

import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;

/**
 * Represents the result of validating an order.
 */
public class OrderValidationResult {
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    public OrderValidationResult(OrderStatus orderStatus, OrderValidationCode orderValidationCode) {
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
    }

    // Getters and setters
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }
}
