package com.jspstudio.maple

object Item {
    // 노가다 목장갑
    fun nogadaGlove() : ItemOption {
        val item = ItemOption()
        item.name = "노가다 목장갑"
        item.reqLev = 10
        item.reqStr = 0
        item.reqDex = 0
        item.reqInt = 0
        item.reqLuk = 0

        item.type = "장갑"
        item.physicalDef = 2
        item.upgradeCnt = 5
        item.img = R.drawable.ic_nogada_glove_item
        item.img_main = R.drawable.ic_nogada_glove

        for (i in 1 .. 5) item.equip.add(i) // 전체직업

        return item
    }

    // 허름한 망토
    fun humang() : ItemOption {
        val item = ItemOption()
        item.name = "허름한 망토"
        item.reqLev = 25 // 착용레벨
        item.reqStr = 0
        item.reqDex = 0
        item.reqInt = 0
        item.reqLuk = 0

        item.type = "망토"
        item.physicalDef = 5 // 물리 방어력
        item.magicDef = 10   // 마법 방어력
        item.avd = 10        // 회피율
        item.upgradeCnt = 5  // 업그레이드 횟수
        item.img = R.drawable.humang_item
        item.img_main = R.drawable.humang

        for (i in 1 .. 5) item.equip.add(i) // 전체직업

        return item
    }
}