package yougboyclub.honbabstop.dto;


import lombok.*;
import yougboyclub.honbabstop.domain.Board;
import yougboyclub.honbabstop.domain.User;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class RequestBoardDto {

    private User writer;
    private String title;
    private String content;
    private LocalTime time;
    private String foodCategory;
    private String placeCategory;
    private int status;
    private int people;
    private String restaurantName;
    private String restaurantAddress;
    private Long hit;
    private int status;
    private String locationX;
    private String locationY;

    public RequestBoardDto(Long id) {
    }


    public Board toEntity() {
        return Board.builder()
                .writer(writer)
                .title(title)
                .content(content)
                .time(time)
                .foodCategory(foodCategory)
                .placeCategory(placeCategory)
                .status(status)
                .people(people)
                .restaurantName(restaurantName)
                .restaurantAddress(restaurantAddress)
                .hit(hit)
                .status(status)
                .locationX(locationX)
                .locationY(locationY)
                .build();
    };
}
