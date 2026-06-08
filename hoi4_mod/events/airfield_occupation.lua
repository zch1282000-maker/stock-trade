namespace = "unlimited_fronts_airfields"

# 当游戏开始时触发
country_event = {
    id = unlimited_fronts_airfields.1
    title = "EVTNAME_unlimited_fronts_airfields_1"
    desc = "EVTDESC_unlimited_fronts_airfields_1"
    picture = "generic_event"
    
    is_triggered_only = yes
    
    trigger = {
        ai = yes
    }
    
    immediate = {
        # 禁用AI空军占领机场
        set_variable = {
            name = "ai_airfield_occupation"
            value = no
        }
    }
}

# 全局事件，在游戏开始时为所有AI国家触发
on_game_start = {
    events = {
        unlimited_fronts_airfields.1
    }
}
