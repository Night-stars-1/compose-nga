/**
 * 生成NGA表情及EmojiUtils对应的代码
 * 代码在EmojiUtils.code文件里
 */

const smiles = {
  0: {
    _______name: "默认",
    1: "smile.gif",
    2: "mrgreen.gif",
    3: "question.gif",
    4: "wink.gif",
    5: "redface.gif",
    6: "sad.gif",
    7: "cool.gif",
    8: "crazy.gif",
    32: "12.gif",
    33: "13.gif",
    34: "14.gif",
    30: "10.gif",
    29: "09.gif",
    28: "08.gif",
    27: "07.gif",
    26: "06.gif",
    25: "05.gif",
    24: "04.gif",
    35: "15.gif",
    36: "16.gif",
    37: "17.gif",
    38: "18.gif",
    39: "19.gif",
    40: "20.gif",
    41: "21.gif",
    42: "22.gif",
    43: "23.gif",
  },

  ac: {
    _______name: "AC娘(v1)",
    //____display:'茶	ac	晕	ac	瞎	ac	大哭	a2	喘	ac	喷	ac	鬼脸	a2	羡慕	ac	闪光	ac	blink	ac	lucky	a2	goodjob	ac	惊	ac	吻	ac	不明觉厉	a2	咦	ac	汗	ac	诶嘿	a2	呆	ac	上	ac	冷	ac	偷笑	ac	有何贵干	a2	怒	a2	中枪	ac	哭1	ac	囧	ac	委屈	ac	怒	ac	哭	a2	愁	ac	抓狂	ac	黑枪	ac	反对	ac	那个…	a2	哦嗬嗬嗬	a2	恨	a2	哭笑	ac	中枪	a2	囧	a2	doge	a2	自戳双目	a2	偷吃	a2	抠鼻	ac	嘲笑1	ac	冷笑	a2	壁咚	a2',
    blink: "ac0.png",
    goodjob: "ac1.png",
    上: "ac2.png",
    中枪: "ac3.png",
    偷笑: "ac4.png",
    冷: "ac5.png",
    凌乱: "ac6.png",
    吓: "ac8.png",
    吻: "ac9.png",
    呆: "ac10.png",
    咦: "ac11.png",
    哦: "ac12.png",
    哭: "ac13.png",
    哭1: "ac14.png",
    哭笑: "ac15.png",
    喘: "ac17.png",
    心: "ac23.png",

    囧: "ac21.png",
    晕: "ac33.png",
    汗: "ac34.png",
    瞎: "ac35.png",
    羞: "ac36.png",
    羡慕: "ac37.png",

    委屈: "ac22.png",
    忧伤: "ac24.png",
    怒: "ac25.png",
    怕: "ac26.png",
    惊: "ac27.png",
    愁: "ac28.png",
    抓狂: "ac29.png",
    哼: "ac16.png",
    喷: "ac18.png",
    嘲笑: "ac19.png",
    嘲笑1: "ac20.png",

    抠鼻: "ac30.png",
    无语: "ac32.png",
    衰: "ac40.png",

    黑枪: "ac44.png",
    花痴: "ac38.png",
    闪光: "ac43.png",
    擦汗: "ac31.png",
    茶: "ac39.png",
    计划通: "ac41.png",
    反对: "ac7.png",
    赞同: "ac42.png",
  },

  a2: {
    _______name: "AC娘(v2)",
    //____display:'哭	ac	哦	ac	干杯	a2	干杯2	a2	冷	a2	羞	ac	惊	a2	花痴	ac	笑	a2	无语	ac	忧伤	ac	擦汗	ac	怕	ac	哼	ac	赞同	ac	心	ac	舔	a2	goodjob	a2	衰	ac	计划通	ac	妮可妮可妮	a2	不活了	a2	是在下输了	a2	你为猴这么	a2	异议	a2	认真	a2	你已经死了	a2	你这种人…	a2	抢镜头	a2	yes	a2	病娇	a2	你看看你	a2	poi	a2	囧2	a2	威吓	a2	嘲笑	ac	jojo立	a2	jojo立2	a2	jojo立3	a2	jojo立4	a2	jojo立5	a2	凌乱	ac	吓	ac	偷笑	a2',
    goodjob: "a2_02.png",
    诶嘿: "a2_05.png",
    偷笑: "a2_03.png",
    怒: "a2_04.png",
    笑: "a2_07.png",
    "那个…": "a2_08.png",
    哦嗬嗬嗬: "a2_09.png",
    舔: "a2_10.png",
    鬼脸: "a2_14.png",
    冷: "a2_16.png",
    大哭: "a2_15.png",
    哭: "a2_17.png",
    恨: "a2_21.png",
    中枪: "a2_23.png",
    囧: "a2_24.png",
    你看看你: "a2_25.png",
    doge: "a2_27.png",
    自戳双目: "a2_28.png",
    偷吃: "a2_30.png",
    冷笑: "a2_31.png",
    壁咚: "a2_32.png",
    不活了: "a2_33.png",
    不明觉厉: "a2_36.png",
    是在下输了: "a2_51.png",
    你为猴这么: "a2_53.png",
    干杯: "a2_54.png",
    干杯2: "a2_55.png",
    异议: "a2_47.png", //"逆转裁判",
    认真: "a2_48.png", //"埼玉老师",
    你已经死了: "a2_45.png", //"拳四郞",
    "你这种人…": "a2_49.png", //"北方栖姬",
    妮可妮可妮: "a2_18.png", //"矢泽妮可",
    惊: "a2_19.png", //"原田海未",
    抢镜头: "a2_52.png", //"南小鸟",
    yes: "a2_26.png", //"佐仓千代",
    有何贵干: "a2_11.png", //"在下坂本",
    病娇: "a2_12.png", //"我妻由乃",
    lucky: "a2_13.png", //"泉此方",
    poi: "a2_20.png", //"夕立",
    囧2: "a2_22.png", //"樱桃小丸子",
    威吓: "a2_42.png", //"时崎狂三",
    jojo立: "a2_37.png", //"仗助",
    jojo立2: "a2_38.png", //"承太郎",
    jojo立3: "a2_39.png", //"乔斯达",
    jojo立4: "a2_41.png", //"迪奥",
    jojo立5: "a2_40.png", //"卡兹"
  },

  ng: {
    _______name: "NG娘",
    呲牙笑: "ng_1.png",
    奸笑: "ng_2.png", //阿尼亚
    问号: "ng_3.png",
    茶: "ng_4.png",
    笑指: "ng_5.png",
    燃尽: "ng_6.png",
    晕: "ng_7.png",
    扇笑: "ng_8.png",
    寄: "ng_9.png",
    别急: "ng_10.png",

    doge: "ng_11.png",
    丧: "ng_12.png",
    汗: "ng_13.png",
    //'呼':'ng_14.png',
    叹气: "ng_15.png",
    吃饼: "ng_16.png",
    吃瓜: "ng_17.png",
    吐舌: "ng_18.png",
    哭: "ng_19.png",
    喘: "ng_20.png",
    心: "ng_21.png",
    喷: "ng_22.png",
    //'斜眼':'ng_23.png',
    困: "ng_24.png",
    大哭: "ng_25.png",
    大惊: "ng_26.png",
    害怕: "ng_27.png",
    惊: "ng_28.png",
    //'晕':'ng_29.png',
    暴怒: "ng_30.png",
    气愤: "ng_31.png",
    热: "ng_32.png",
    瓜不熟: "ng_33.png",
    瞎: "ng_34.png",
    色: "ng_35.png",
    //'茶':'ng_36.png',
    斜眼: "ng_37.png",
    问号大: "ng_38.png",
  },

  pst: {
    _______name: "潘斯特",
    举手: "pt00.png",
    亲: "pt01.png",
    偷笑: "pt02.png",
    偷笑2: "pt03.png",
    偷笑3: "pt04.png",
    傻眼: "pt05.png",
    傻眼2: "pt06.png",
    兔子: "pt07.png",
    发光: "pt08.png",
    呆: "pt09.png",
    呆2: "pt10.png",
    呆3: "pt11.png",
    呕: "pt12.png",
    呵欠: "pt13.png",
    哭: "pt14.png",
    哭2: "pt15.png",
    哭3: "pt16.png",
    嘲笑: "pt17.png",
    基: "pt18.png",
    宅: "pt19.png",
    安慰: "pt20.png",
    幸福: "pt21.png",
    开心: "pt22.png",
    开心2: "pt23.png",
    开心3: "pt24.png",
    怀疑: "pt25.png",
    怒: "pt26.png",
    怒2: "pt27.png",
    怨: "pt28.png",
    惊吓: "pt29.png",
    惊吓2: "pt30.png",
    惊呆: "pt31.png",
    惊呆2: "pt32.png",
    惊呆3: "pt33.png",
    惨: "pt34.png",
    斜眼: "pt35.png",
    晕: "pt36.png",
    汗: "pt37.png",
    泪: "pt38.png",
    泪2: "pt39.png",
    泪3: "pt40.png",
    泪4: "pt41.png",
    满足: "pt42.png",
    满足2: "pt43.png",
    火星: "pt44.png",
    牙疼: "pt45.png",
    电击: "pt46.png",
    看戏: "pt47.png",
    眼袋: "pt48.png",
    眼镜: "pt49.png",
    笑而不语: "pt50.png",
    紧张: "pt51.png",
    美味: "pt52.png",
    背: "pt53.png",
    脸红: "pt54.png",
    脸红2: "pt55.png",
    腐: "pt56.png",
    星星眼: "pt57.png",
    谢: "pt58.png",
    醉: "pt59.png",
    闷: "pt60.png",
    闷2: "pt61.png",
    音乐: "pt62.png",
    黑脸: "pt63.png",
    鼻血: "pt64.png",
  },

  dt: {
    _______name: "外域三人组",
    ROLL: "dt01.png",
    上: "dt02.png",
    傲娇: "dt03.png",
    叉出去: "dt04.png",
    发光: "dt05.png",
    呵欠: "dt06.png",
    哭: "dt07.png",
    啃古头: "dt08.png",
    嘲笑: "dt09.png",
    心: "dt10.png",
    怒: "dt11.png",
    怒2: "dt12.png",
    怨: "dt13.png",
    惊: "dt14.png",
    惊2: "dt15.png",
    无语: "dt16.png",
    星星眼: "dt17.png",
    星星眼2: "dt18.png",
    晕: "dt19.png",
    注意: "dt20.png",
    注意2: "dt21.png",
    泪: "dt22.png",
    泪2: "dt23.png",
    烧: "dt24.png",
    笑: "dt25.png",
    笑2: "dt26.png",
    笑3: "dt27.png",
    脸红: "dt28.png",
    药: "dt29.png",
    衰: "dt30.png",
    鄙视: "dt31.png",
    闲: "dt32.png",
    黑脸: "dt33.png",
  },

  pg: {
    _______name: "企鹅",
    战斗力: "pg01.png",
    哈啤: "pg02.png",
    满分: "pg03.png",
    衰: "pg04.png",
    拒绝: "pg05.png",
    心: "pg06.png",
    严肃: "pg07.png",
    吃瓜: "pg08.png",
    嘣: "pg09.png",
    嘣2: "pg10.png",
    冻: "pg11.png",
    谢: "pg12.png",
    哭: "pg13.png",
    响指: "pg14.png",
    转身: "pg15.png",
  },
}; //s

import { createWriteStream, unlinkSync, writeFileSync } from 'fs';
import { get } from 'https';
import { resolve, dirname, parse } from 'path';

function downloadImage(url, outputPath) {
    const file = createWriteStream(outputPath);
    get(url, (response) => {
        response.pipe(file);
        file.on('finish', () => {
            file.close();
        });
    }).on('error', (err) => {
        unlinkSync(outputPath);
        console.error('下载失败:', err.message);
    });
}

let code = ""
for (const smileId in smiles) {
    const smileData = smiles[smileId]
    for (const smileName in smileData) {
        const smileTitle = smileData._______name
        let urlPath = smileData[smileName]
        if (smileName == "_______name") {
            console.log(urlPath)
            continue
        }
        const url = `https://img4.nga.178.com/ngabbs/post/smile/${urlPath}`
        if (smileTitle == "默认") {
            urlPath = `default_${urlPath}`
        }
        const name = parse(urlPath).name
        // console.log(`[s:${smileId}:${smileName}]`, url)
        downloadImage(url, resolve(dirname(import.meta.filename), "../app/src/main/res/drawable", urlPath));
        code += `emojiMap["[s:${smileId}:${smileName}]"] = R.drawable.${name}\n`
    }
}

writeFileSync('EmojiUtils.code', code, 'utf8');
