package cn.edu.sjtu.icat.smartparking;

/**
 * Created by Ruochen on 2015/10/20.
 */
public class OrderListElement {
    public static final int TYPE_TAG_CONFIRMED = 1;
    public static final int TYPE_TAG_FINISHED = 2;
    public static final int TYPE_ORDER_CONFIRMED = 3;
    public static final int TYPE_ORDER_FINISHED = 4;
    public static final int TYPE_EMPTY = 0;

    private int mElementType;
    private Order mOrder;

    public OrderListElement (int type) {
        mElementType = type;
    }
    public OrderListElement () {

    }

    public int getElementType() {
        return mElementType;
    }

    public void setElementType(int elementType) {
        mElementType = elementType;
    }

    public Order getOrder() {
        return mOrder;
    }

    public void setOrder(Order order) {
        mOrder = order;
    }
}
