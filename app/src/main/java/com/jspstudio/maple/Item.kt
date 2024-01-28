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

        item.book_10 = "장공 주문서 10% (공+3)"
        item.book_60 = "장공 주문서 60% (공+2)"

        item.att_10 = 3
        item.att_60 = 2

        item.type = "장갑"
        item.physicalDef = 2
        item.upgradeCnt = 5
        item.img = R.drawable.ic_nogada_glove_item
        item.imgMain = R.drawable.ic_nogada_glove

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

        item.book_10 = "망민 주문서 10% (DEX+3)"
        item.book_60 = "망민 주문서 60% (DEX+2)"

        item.dex_10 = 3
        item.dex_60 = 2

        item.type = "망토"
        item.physicalDef = 5  // 물리 방어력
        item.magicDef = 10    // 마법 방어력
        item.avd = 10         // 회피율
        item.upgradeCnt = 10  // 업그레이드 횟수
        item.img = R.drawable.humang_item
        item.imgMain = R.drawable.humang

        for (i in 1 .. 5) item.equip.add(i) // 전체직업

        return item
    }

    // 파란색 가운
    fun blueGown() : ItemOption {
        val item = ItemOption()
        item.name = "파란색 가운 (남)"
        item.reqLev = 30 // 착용레벨
        item.reqStr = 0
        item.reqDex = 0
        item.reqInt = 0
        item.reqLuk = 0

        item.book_10 = "전민 주문서 10% \n(DEX+5)\n(명중 +3)\n(이속 +1)"
        item.book_60 = "전민 주문서 60% \n(DEX+2)\n(명중 +1)"

        item.dex_10 = 5
        item.dex_60 = 2
        item.acc_10 = 3
        item.acc_60 = 1
        item.speed_10 = 1

        item.type = "한벌옷"
        item.physicalDef = 30  // 물리 방어력
        item.avd = 10          // 회피율
        item.upgradeCnt = 10   // 업그레이드 횟수
        item.img = R.drawable.blue_gown_item
        item.imgMain = R.drawable.blue_gown

        for (i in 1 .. 5) item.equip.add(i) // 전체직업

        return item
    }

    // 빨간색 가운
    fun redGown() : ItemOption {
        val item = ItemOption()
        item.name = "빨간색 가운 (여)"
        item.reqLev = 30 // 착용레벨
        item.reqStr = 0
        item.reqDex = 0
        item.reqInt = 0
        item.reqLuk = 0

        item.book_10 = "전민 주문서 10% \n(DEX+5)\n(명중 +3)\n(이속 +1)"
        item.book_60 = "전민 주문서 60% \n(DEX+2)\n(명중 +1)"

        item.dex_10 = 5
        item.dex_60 = 2
        item.acc_10 = 3
        item.acc_60 = 1
        item.speed_10 = 1

        item.type = "한벌옷"
        item.physicalDef = 30  // 물리 방어력
        item.avd = 10          // 회피율
        item.upgradeCnt = 10   // 업그레이드 횟수
        item.img = R.drawable.red_gown_item
        item.imgMain = R.drawable.red_gown

        for (i in 1 .. 5) item.equip.add(i) // 전체직업

        return item
    }
}