package com.example.composeanimationdemo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 简单值动画, animateColorAsState, 另外还有很多以animate开头作为状态AsState的动画
 */
@Composable
fun Demo1() {
    val (change, setChange) = remember {
        mutableStateOf(false)
    }
    val background by animateColorAsState(if (change) Color.Gray else Color.Blue)
//    val background = if (change) Color.Gray else Color.Blue
    Column {
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .background(background),
        )
        Text(text = "点击变换颜色", modifier = Modifier.clickable { setChange(!change) })
    }
}

/**
 * 可见性动画, AnimatedVisibility
 */
@Composable
fun Demo2() {
    val (change, setChange) = remember {
        mutableStateOf(true)
    }
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
        Text(text = "点击改变可见性", modifier = Modifier.clickable { setChange(!change) })
    }
}

/**
 * 可见性动画, AnimatedVisibility，自定义进入进出,Offset不好理解
 */
@Composable
fun Demo3() {
    val (change, setChange) = remember {
        mutableStateOf(true)
    }
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
        Text(text = "点击改变可见性", modifier = Modifier.clickable { setChange(!change) })
    }
}

/**
 * 内容大小动画, animateContentSize
 */
@Composable
fun Demo4() {
    val (change, setChange) = remember {
        mutableStateOf(false)
    }
    val background = Color.Gray
    Column(modifier = Modifier.animateContentSize()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { setChange(!change) })
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
    val (change, setChange) = remember {
        mutableStateOf(false)
    }
    val transition = updateTransition(targetState = change, label = "多值动画")
    val offset by transition.animateDp(label = "") { change ->
        if (change) 50.dp else 0.dp
    }
    val background by transition.animateColor(label = "") { change ->
        if (change) Color.Gray else Color.Blue
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { setChange(!change) })
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
    val (change, setChange) = remember {
        mutableStateOf(false)
    }
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
        Text(text = "点击改变内容大小", modifier = Modifier.clickable { setChange(!change) })
        Text(
            text = "背景颜色：${background}",
            modifier = Modifier
                .size(100.dp)
                .offset(x = offset)
                .background(background),
        )
    }
}