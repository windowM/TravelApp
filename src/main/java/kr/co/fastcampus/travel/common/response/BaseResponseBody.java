package kr.co.fastcampus.travel.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BaseResponseBody {
    private final Status status;
    private final String errorMessage;

    public static BaseResponseBody ok() {
        return new BaseResponseBody(Status.SUCCESS, null);
    }

    public static BaseResponseBody fail(String errorMessage) {
        return new BaseResponseBody(Status.FAIL, errorMessage);
    }

    public static BaseResponseBody error(String errorMessage) {
        return new BaseResponseBody(
            Status.ERROR,
            "예상하지 못한 오류입니다. 관리자에게 문의해주세요: " + errorMessage
        );
    }
}
