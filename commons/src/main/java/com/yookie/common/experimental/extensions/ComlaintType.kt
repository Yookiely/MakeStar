package com.yookie.common.experimental.extensions

/**
 * type分为1-10
 * 1.投诉用户
 * 2.投诉作品
 * 3.投诉作品一级评论
 * 4.投诉作品二级评论
 * 5.投诉动态
 * 6.投诉动态一级评论
 * 7.投诉动态二级评论
 * 8.投诉粉丝圈动态
 * 9.投诉粉丝圈动态一级评论
 * 10.投诉粉丝圈动态二级评论
 */

object ComplaintType {
    const val COMPLAINT_TYPE = "complaintType"
    const val COMPLAINT_ID = "complaintId"


    const val USER: Int = 1;
    const val WORK: Int = 2;
    const val COMMENT_WORK_FIRST: Int = 3;
    const val COMMENT_WORK_SECOND: Int = 4;
    const val LINE: Int = 5;
    const val COMMENT_LINE_FIRST: Int = 6;
    const val COMMENT_LINE_SECOND: Int = 7;
    const val FRIEND_CIRLE: Int = 8;
    const val COMMENT_FRIEND_CIRLE_FIRST: Int = 9;
    const val COMMENT_FRIEND_CIRLE_SECOND: Int = 10;

}