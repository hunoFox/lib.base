package com.hunofox.baseFramework.widget.contactView;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Comparator;

/**
 * 项目名称：TongTong
 * 项目作者：胡玉君
 * 创建日期：2019-09-16 11:49.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class ContactComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        String pinyin1 = Pinyin.toPinyin(o1,"");
        String pinyin2 = Pinyin.toPinyin(o2,"");
        int c1 = (pinyin1.charAt(0) + "").toUpperCase().hashCode();
        int c2 = (pinyin2.charAt(0) + "").toUpperCase().hashCode();

        boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
        boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母
        if (c1Flag && !c2Flag) {
            return 1;
        } else if (!c1Flag && c2Flag) {
            return -1;
        }

        return c1 - c2;
    }
}
