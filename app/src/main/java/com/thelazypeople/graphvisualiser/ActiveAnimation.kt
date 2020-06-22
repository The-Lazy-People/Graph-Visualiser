package com.thelazypeople.graphvisualiser

import com.airbnb.lottie.LottieAnimationView

class ActiveAnimation(var anim: LottieAnimationView) {

    companion object{
        const val Inactive_to_active = 1f
        const val Active_to_inactive = -1f
    }

    init {
        anim.setAnimation("active_button.json")
    }

    fun toActive(){
        anim.speed = Inactive_to_active
        anim.loop(false)
        anim.playAnimation()
    }

    fun toInactive(){
        anim.speed = Active_to_inactive
        anim.loop(false)
        anim.playAnimation()
    }

}