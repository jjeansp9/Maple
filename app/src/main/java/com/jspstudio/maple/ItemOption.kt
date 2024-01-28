package com.jspstudio.maple

data class ItemOption(
    var reqLev : Int = 0,
    var reqStr : Int = 0,
    var reqDex : Int = 0,
    var reqInt : Int = 0,
    var reqLuk : Int = 0,

    var type : String? = "",    // 장비분류
    var physicalDef : Int = -1, // 물리방어력
    var magicDef : Int = -1,    // 마법방어력
    var physicalAtt : Int = -1, // 물리공격력
    var magicAtt : Int = -1,    // 마법공격력
    var avd : Int = -1,         // 회피율
    var speed : Int = -1,       // 이속
    var attSpeed : Int = -1,    // 공속
    var jump : Int = -1,        // 점프력
    var acc : Int = -1,         // 명중률
    var crt : Int = -1,         // 크리티컬
)
