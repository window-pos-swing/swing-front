package org.grr.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class OrderData {
    public static List<OrderCategory> createSampleData() {
        return Arrays.asList(
                new OrderCategory(LocalDateTime.now(), "B4C845562444566", "배달", "결제완료", 123456, "카드결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "배달", "결제완료", 123456, "카드결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "포장", "결제완료", 123456, "만나서 현금결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "배달", "결제완료", 123456, "만나서 카드결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "배달", "결제완료", 123456, "카드결제"),
                new OrderCategory(LocalDateTime.now(), "B4C845562444566", "배달", "결제취소", 123456, "카드결제"),
                new OrderCategory(LocalDateTime.now(), "B4C845562444566", "포장", "결제완료", 123456, "만나서 현금결제"),
                new OrderCategory(LocalDateTime.now(), "B4C845562444566", "포장", "결제취소", 123456, "만나서 카드결제"),
                new OrderCategory(LocalDateTime.now(), "B4C845562444566", "포장", "결제완료", 123456, "카드결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "배달", "결제취소", 123456, "만나서 현금결제"),
                new OrderCategory(LocalDateTime.now().minusDays(1), "B4C845562444566", "배달", "결제완료", 123456, "만나서 카드결제")
        );
    }
}
