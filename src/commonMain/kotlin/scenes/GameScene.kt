package scenes

import korlibs.event.*
import korlibs.image.color.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.time.*

class GameScene: Scene() {

    override suspend fun SContainer.sceneMain() {
        var playerPoints = 0
        var enemyPoints = 0

        //Text
        val enemyPointText = text("Points: $enemyPoints", textSize = 15.0F)
            .addTo(this)
            .alignTopToTopOf(this)
            .alignLeftToLeftOf(this)
        enemyPointText.y += 20f

        val playerPointText = text("Points: $playerPoints", textSize = 15.0F)
            .addTo(this)
            .alignBottomToBottomOf(this)
            .alignRightToRightOf(this)
        playerPointText.y -= 20f

        // Player rectangle
        val player = solidRect(globalBounds.width / 4f, 20f, Colors.WHITE)
            .addTo(this)
        player.xy(0f, globalBounds.height - player.height) //Position is at the bottom of the screen

        //Enemy rectangle
        val enemy = solidRect(globalBounds.width / 4f, 20f, Colors.WHITE)
            .addTo(this)
            .apply {
                xy(0f, 0f) //at the top of the screen
            }

        //Ball
        val ball = circle(16f, Colors.WHITE)
            .addTo(this)
            .xy(globalBounds.width / 2f, globalBounds.height / 2f) //in the middle of the screen

        //Velocity of the ball
        var velocity = vec(0f, 1f)

        //Add updater callback (called on each frame). TimeSpan is the time elapsed since the last frame
        addUpdater { timespan: TimeSpan ->
            val scale = timespan / 3.milliseconds

            //Key input
            if(input.keys[Key.LEFT]) {
                player.x -= kotlin.math.min(scale, player.x) //When left is pressed, move left, but don't go out of the screen
            }
            if(input.keys[Key.RIGHT]) {
                player.x += kotlin.math.min(scale, globalBounds.width - player.x - player.width) //When right is pressed, move right, but don't go out of the screen
            }

            //Mouse input
            //player.x = input.mousePos.x

            //Touch input
            input.touches.forEach {
                if(it.y < globalBounds.height / 2f) {
                    enemy.x = kotlin.math.min(it.x, globalBounds.width - enemy.width)
                } else {
                    player.x = kotlin.math.min(it.x, globalBounds.width - player.width)
                }
            }

            if(ball.collidesWith(player, CollisionKind.GLOBAL_RECT)) { //Check if the ball collides with the player
                velocity = vec((ball.x - player.x) / player.width, -velocity.y)
            } else if(ball.collidesWith(enemy, CollisionKind.GLOBAL_RECT)) {
                velocity = vec((ball.x - enemy.x) / enemy.width, -velocity.y)
            }
            //Check for wall collisions
            if(ball.x < 0 || ball.x > globalBounds.width - ball.width) {
                velocity = vec(-velocity.x, velocity.y)
            }

            //Check for top and bottom collisions
            if(ball.y >= globalBounds.height - ball.height) {
                ball.pos = vec(globalBounds.width / 2f, globalBounds.height / 2f)
                velocity = vec(0f, 1f)
                enemyPoints++
                enemyPointText.text = "Points: $enemyPoints"
            } else if(ball.y <= 0) {
                ball.pos = vec(globalBounds.width / 2f, globalBounds.height / 2f)
                velocity = vec(0f, 1f)
                playerPoints++
                playerPointText.text = "Points: $playerPoints"
            }
            ball.pos += velocity * scale
//            if (enemy.x + enemy.width / 2f > ball.x)
//                enemy.x -= kotlin.math.min(2f, enemy.x + enemy.width / 2f - ball.x)
//            else
//                enemy.x += kotlin.math.min(2f, ball.x - (enemy.x + enemy.width / 2f))
        }
    }

}
