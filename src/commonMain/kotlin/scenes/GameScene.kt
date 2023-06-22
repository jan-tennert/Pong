package scenes

import korlibs.event.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.tween.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import korlibs.time.*
import scenes.*
import kotlin.random.*

class GameScene: Scene() {

    override suspend fun SContainer.sceneMain() {
        var points = 0

        //Text
        val text = text("Points: $points", textSize = 20.0F)
            .addTo(this)
            .alignTopToTopOf(this)
            .alignLeftToLeftOf(this)

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
            if(input.keys[Key.LEFT]) {
                player.x -= kotlin.math.min(scale, player.x) //When left is pressed, move left, but don't go out of the screen
            }
            if(input.keys[Key.RIGHT]) {
                player.x += kotlin.math.min(scale, globalBounds.width - player.x - player.width) //When right is pressed, move right, but don't go out of the screen
            }
            if(ball.collidesWith(player)) { //Check if the ball collides with the player
                velocity = vec((ball.x - player.x) / player.width, -velocity.y)
            } else if(ball.collidesWith(enemy)) {
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
                points = 0
                text.text = "Points: $points"
            } else if(ball.y <= 0) {
                ball.pos = vec(globalBounds.width / 2f, globalBounds.height / 2f)
                velocity = vec(0f, 1f)
                points += 1
                text.text = "Points: $points"
            }
            ball.pos += velocity * scale
            if (enemy.x + enemy.width / 2f > ball.x)
                enemy.x -= kotlin.math.min(2f, enemy.x + enemy.width / 2f - ball.x)
            else
                enemy.x += kotlin.math.min(2f, ball.x - (enemy.x + enemy.width / 2f))
        }
    }

}
