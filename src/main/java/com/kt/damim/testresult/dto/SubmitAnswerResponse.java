package com.kt.damim.testresult.dto;

/**
 * 제출 후 다음 문항 이동/종료 플래그
 */
public record SubmitAnswerResponse(
    boolean ok,
    Integer nextIdx,
    Boolean isFinished
) {
    public static SubmitAnswerResponse next(int nextIdx) {
        return new SubmitAnswerResponse(true, nextIdx, false);
    }
    public static SubmitAnswerResponse finished() {
        return new SubmitAnswerResponse(true, null, true);
    }
}
