package com.example.composeanimationdemo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * 简单值动画, animateColorAsState, 另外还有很多以animate开头作为状态AsState的动画
 */
@Composable
fun Demo1() {
    var change by remember { mutableStateOf(false) }
    val background by animateColorAsState(if (change) Color.Gray else Color.Blue)
//    val background = if (change) Color.Gray else Color.Blue
    Column {
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .background(background),
        )
        Text(text = "点击变换颜色", modifier = Modifier.clickable { change = !change })
    }
}

/**
 * 可见性动画, AnimatedVisibility
 */
@Composable
fun Demo2() {
    var change by remember { mutableStateOf(false) }
    val background = Color.Gray
    Column {
        AnimatedVisibility(visible = change) {
            Text(
                text = "背景颜色：${background}",
                modifier = Modifier
                    .size(100.dp)
                    .background(background),
            )
        }
        Text(text = "点击改变可见性", modifier = Modifier.clickable { change = !change })
    }
}

/**
 * 可见性动画, AnimatedVisibility，自定义进入进出,Offset不好理解
 */
@Composable
fun Demo3() {
    var change by remember { mutableStateOf(false) }
    val background = Color.Gray
    Column {
        AnimatedVisibility(
            visible = change,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
                //LinearOutSlowInEasing:传入元素使用减速缓和设置动画，减速缓和以峰值速度（元素移动的最快点）开始过渡，并在静止时结束
                animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
                //FastOutSlowInEasing:退出屏幕的元素使用加速度缓和，从静止开始，以峰值速度结束
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
        ) {
            Text(
                text = "背景颜色：${background}",
                modifier = Modifier
                    .size(100.dp)
                    .background(background),
            )
        }
        Text(text = "点击改变可见性", modifier = Modifier.clickable { change = !change })
    }
}

/**
 * 内容大小动画, animateContentSize
 */
@Composable
fun Demo4() {
    var change by remember { mutableStateOf(false) }
    val background = Color.Gray
    Column(modifier = Modifier.animateContentSize()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { change = !change })
        if (change) {
            Text(
                text = "背景颜色：${background}",
                modifier = Modifier
                    .size(100.dp)
                    .background(background),
            )
        }
    }
}

/**
 * 多值动画, updateTransition，当状态发生改变时，有多个动画值要一起发生改变
 * 设置一个Transition，并使用targetState提供的目标对其进行更新。
 * 当targetState更改时，Transition将朝着新targetState指定的目标值运行其所有子动画
 * 可以使用Transition动态添加子动画：Transition.animateFloat、animateColor、animateValue等
 */
@Composable
fun Demo5() {
    var change by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = change, label = "多值动画")
    val offset by transition.animateDp(label = "") { change ->
        if (change) 50.dp else 0.dp
    }
    val background by transition.animateColor(label = "") { change ->
        if (change) Color.Gray else Color.Blue
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { change = !change })
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .offset(x = offset)
                .background(background),
        )
    }
}

/**
 * 多值动画, updateTransition，当状态发生改变时，有多个动画值要一起发生改变
 * 弹性效果，transitionSpec
 */
@Composable
fun Demo6() {
    var change by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = change, label = "多值动画")
    val offset by transition.animateDp(
        transitionSpec = {
            //从左往右，左边缘比右边缘移动得慢
            if (!change isTransitioningTo change) {
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = ""
    ) { change ->
        if (change) 50.dp else 0.dp
    }
    val background by transition.animateColor(label = "") { change ->
        if (change) Color.Gray else Color.Blue
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { change = !change })
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .offset(x = offset)
                .background(background),
        )
    }
}

/**
 * 重复动画，rememberInfiniteTransition
 */
@Composable
fun Demo7() {
    var change by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val background = if (change) Color.Gray else Color.Blue
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { change = !change })
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .alpha(alpha)
                .background(background),
        )
    }
}

/**
 * 手势动画，滑动删除
 */
@Composable
fun Demo8() {
    Text(text = "滑动删除",
        modifier = Modifier
            .swipeToDismiss { }
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.LightGray)
            //文字竖直居中
            .wrapContentHeight(Alignment.CenterVertically)
    )
}

fun Modifier.swipeToDismiss(onDismissed: () -> Unit): Modifier = composed {
    composed {
        //水平位移
        val offsetX = remember {
            Animatable(0f)
        }
        //使用 pointerInput 修饰符，可以获取对传入指针触摸事件的低级别访问，并跟踪用户使用同一指针拖动的速度。
        pointerInput(Unit) {
            //衰减动画，用于计算滑动动画的最终停止位置
            val decay = splineBasedDecay<Float>(this)
            //开启协程监听触摸事件
            coroutineScope {
                //使用while (true)一直监听触摸事件
                while (true) {
                    //等待触摸按下事件
                    //awaitPointerEventScope：挂起并安装指针输入块，该块可以等待输入事件并立即响应它们
                    //awaitFirstDown：第一个down事件
                    val pointerId = awaitPointerEventScope {
                        awaitFirstDown().id
                    }
                    //如果动画当前正在运行，我们应将其拦截。可以通过对 Animatable 调用 stop 来实现此目的。
                    // 请注意，如果动画未运行，系统会忽略该调用。VelocityTracker 用于计算用户从左向右移动的速度。
                    offsetX.stop()
                    val velocityTracker = VelocityTracker()
                    //等待拖动事件
                    awaitPointerEventScope {
                        //监听水平滑动
                        horizontalDrag(pointerId) { change ->
                            val horizontalDragOffset = offsetX.value + change.positionChange().x
                            //只能在另一个 launch 代码块内发起对 snapTo 的调用，
                            // 因为 awaitPointerEventScope 和 horizontalDrag 是受限的协程范围。
                            // 也就是说，它们只能对 awaitPointerEvents 执行 suspend，而 snapTo 并不是指针事件。
                            launch {
                                offsetX.snapTo(horizontalDragOffset)
                            }
                            //记录下时间和位置就可以计算出速度
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                            //消费掉手势事件，而不是传递给外部
                            change.consumePositionChange()
                        }
                    }
                    //滑动事件结束，计算滑动速度
                    val velocity = velocityTracker.calculateVelocity().x
                    //计算最终停止位置
                    val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
                    //给动画设置边界
                    offsetX.updateBounds(
                        lowerBound = -size.width.toFloat(),
                        upperBound = size.width.toFloat()
                    )
                    launch {
                        if (targetOffsetX.absoluteValue <= size.width) {
                            //返回原位
                            offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                        } else {
                            //使用衰减动画直到滑动边界
                            offsetX.animateDecay(velocity, decay)
                            onDismissed()
                        }
                    }
                }
            }
        }
            //应用水平位移给元素
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
    }
}




