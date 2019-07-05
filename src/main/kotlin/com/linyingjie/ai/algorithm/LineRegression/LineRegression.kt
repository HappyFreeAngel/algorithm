package com.lingyingjie.ai.algorithm.LineRegression

import java.util.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

data class Point(val x: Double,val y: Double)

//本文默认情况下，直线的方程为l:Ax+By+C=0，A，B均不为0，斜率为k_l，点的坐标为P(x0，y0)，点P到l的距离为d。
// y=ax+b (直线的另一种表示方法)  <==> ax+(-1)*y+b=0  对照Ax+By+C=0  得出   A=a,B=-1,C=b
class Line(val a: Double,val b:Double,val c:Double)

fun Line.toString():String{
  return ""+a+"x+"+b+"y c="+c;
}

fun createLine(pointA: Point,pointB: Point): Line {
    var a=calculateLineCoefficientFrom2Point(pointA,pointB); //A
    var b=-1.0;//B
    var c=interceptFrom2Point(pointA,pointB);//C
    var line = Line(a,b,c)
    return line;
}

fun getLowPoint(pointA: Point,pointB: Point): Point{
    if(pointA.y>=pointB.y){
        return  pointB
    }
    else {
        return pointA
    }
}

fun getHighPoint(pointA: Point,pointB: Point): Point{
    if(pointA.y>=pointB.y){
        return  pointA
    }
    else {
        return pointB
    }
}


fun get2pointDistance(a: Point,b:Point):Double {
    return  Math.sqrt( (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) )
}

fun Line.getPointToLineverticalDistance(p: Point):Double {
    return  Math.abs(a*p.x+b*p.y+c)/Math.sqrt(a*a+b*b)
}


fun interceptFrom2Point(a: Point,b:Point): Double{

    var highPoint= getHighPoint(a,b)
    var lowPoint= getLowPoint(a,b)
    var lineCoefficient=0.0;
    if(highPoint.x!=lowPoint.x) {
        lineCoefficient = (highPoint.y - lowPoint.y) / (highPoint.x - lowPoint.x)
    }
    else {
        lineCoefficient=0.0
    }

    val intercept=lowPoint.y-lineCoefficient*lowPoint.x

    //println("与X轴的交叉点是:Point(0,${intercept})")
    return intercept
}

fun calculateLineCoefficientFrom2Point(a: Point,b:Point): Double{

    var highPoint= Point(0.0,0.0);
    var lowPoint= Point(0.0,0.0);
    if(a.y>=b.y){
        highPoint=a
        lowPoint=b
    }
    else {
        highPoint=b
        lowPoint=a
    }

    return (highPoint.y-lowPoint.y)/(highPoint.x-lowPoint.x)
}



fun main(args: Array<String>){

    println("Hell world!")
    val pointA = Point(10.0,10.0)
    val pointB = Point(200.0,10.0)
    val pointP = Point(10.0,11.0)

    val line = createLine(pointA,pointB);
    println("点P到线的垂直距离是:"+line.getPointToLineverticalDistance(pointP))

    var data: Array<Point> = Array(6000,{Point(0.0,0.0)});
    for(index in 0 until data.size){
        val fuhao1: Double= if(Math.random()>0.5) {1.0 } else {-1.0}
        val fuhao2: Double= if(Math.random()>0.5) {1.0 } else {-1.0}

        data[index]=Point(Math.random()*100*fuhao1,Math.random()*800*fuhao2)
    }
    val line3=lineRegression(data)
    if(line3!=null) {
        println("" + line3.a + "x+(" + line3.b + ")y+" + line3.c);
    }
    else {
        println("没找到。")
    }
}


//minimizes the sum of  the squared residuals
fun lineRegression(data: Array<Point>):Line?{

    var bestLine: Line? = null
    var bestLineSSR: Double=Double.MAX_VALUE

    var map= ConcurrentHashMap<Line,Double>()


        for(index1 in 0 until  data.size) {
            for (index2 in index1 + 1 until data.size) {
               launch {
                   val line = createLine(data[index1], data[index2])
                   val ssr = line.caculateSSR(data)
                   map.put(line,ssr)
               }
                //有全部跑完吗?
//
//                if(ssr<bestLineSSR){
//                    bestLine = line
//                    bestLineSSR=ssr
//                }
            }
        }

      var mini= map.values.sorted().get(0)
      for(item in map){
          if(item.value==mini){
              bestLine=item.key;
              break;
          }
      }

    return bestLine;
}

//本文默认情况下，直线的方程为l:Ax+By+C=0，A，B均不为0，斜率为k_l，点的坐标为P(x0，y0)，点P到l的距离为d。
// y=ax+b (直线的另一种表示方法)  <==> ax+(-1)*y+b=0
// 对照Ax+By+C=0  得出   A=a,B=-1,C=b
fun Line.caculateSSR(data: Array<Point>):Double{

    var sum=0.0
    for(point in data){
       //val d= getPointToLineverticalDistance(point)
       // a*point.x+b*y+c=0
       var y1=(-a*point.x-c)/b
       sum+= (y1-point.y)*(y1-point.y)
    }

    val ssr=Math.sqrt(sum)
    return ssr
}