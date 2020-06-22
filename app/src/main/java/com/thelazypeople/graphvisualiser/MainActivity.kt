package com.thelazypeople.graphvisualiser

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity(), View.OnTouchListener, View.OnDragListener, AdapterView.OnItemSelectedListener {
    var pointAForLine= PointF(10f,10f)
    var pointBForLine= PointF(10f,10f)
    var stateOfConnection=0
    var getMode=0                       //0-> create node  1-> connection  2-> delete connection
    private val TAG = "TREETAG"
    var lastNodePosition= PointF(0f,0f)
    lateinit var lastNode: ImageView
    var nodes= mutableListOf<ImageView>()
    var nodesFixedOrNot= mutableListOf<Int>()
    var connections= mutableListOf<MutableList<LineView>>()
    var noOfNodes=0
    var algorithm =0
    var links= mutableListOf<MutableList<Int>>()
    var checker= mutableListOf<Int>()
    var index=0
    var isBFSStarterSelected=0
    var startingBFSNode=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)
            ArrayAdapter.createFromResource(
            this,
            R.array.algorithms_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
        visualize.setOnClickListener {
            checker.removeAll(checker)
            for (i in 0..noOfNodes-1) {
                checker.add(0)
            }
            for (i in 0..links.size-1){
                links.removeAt(0)
            }
            for (i in 0..connections.size-1) {
                var linksOfOneNode= mutableListOf<Int>()
                for (j in 0..connections[i].size-1) {
                    if(connections[i][j]!=fakeLineView){
                        linksOfOneNode.add(j)
                    }
                }
                links.add(linksOfOneNode)
            }
            when(algorithm){
                0 -> Toast.makeText(this, "Please select the algorithm first", Toast.LENGTH_SHORT).show()
                1 ->  {
                    Toast.makeText(this, "DFS", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(this, "BFS", Toast.LENGTH_SHORT).show()
                    if(isBFSStarterSelected==0){
                        Toast.makeText(this,"Select Starting Node",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        GlobalScope.launch(Dispatchers.Main) {
                            bfs(startingBFSNode)
                        }
                    }
                }
            }
        }

        rlCanvas.setOnDragListener(this)
        btnRemoveConnection.setBackgroundColor(Color.WHITE)
        btnConnection.setBackgroundColor(Color.WHITE)
        btnAdd.setBackgroundColor(Color.WHITE)
        btnstarting_point.setBackgroundColor(Color.WHITE)

        btnAdd.setOnClickListener {
            if(getMode==0)
                addTV()
        }

        btnConnection.setOnClickListener {
            if(noOfNodes>1) {
                if (stateOfConnection == 0) {
                    if (getMode != 1) {
                        getMode = 1
                        btnConnection.setBackgroundColor(Color.RED)
                    } else {
                        getMode = 0
                        btnConnection.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        }

        btnstarting_point.setOnClickListener {
            getMode=3
            btnAdd.isClickable=false
            btnConnection.isClickable=false
            btnRemoveConnection.isClickable=false
            btnAdd.isClickable=false
            btnstarting_point.setBackgroundColor(Color.RED)
        }
        btnRemoveConnection.setOnClickListener {
            if (noOfNodes>1) {
                if (stateOfConnection == 0) {
                    if (getMode != 2) {
                        getMode = 2
                        btnRemoveConnection.setBackgroundColor(Color.RED)
                    } else {
                        getMode = 0
                        btnRemoveConnection.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        }
        btnReset.setOnClickListener {
            for(i in 0..nodes.size-1){
                rlCanvas.removeView(nodes[i])
            }
            for(i in 0..connections.size-1){
                for (j in 0..connections[i].size-1){
                    if(connections[i][j]!=fakeLineView){
                        rlCanvas.removeView(connections[i][j])
                        connections[i][j]=fakeLineView
                        connections[j][i]=fakeLineView
                    }
                }
            }
            nodes.removeAll(nodes)
            for (i in 0..connections.size-1){
                connections.removeAt(0)
            }
            lastNodePosition= PointF(0f,0f)
            noOfNodes=0
            getMode=0
            nodesFixedOrNot.removeAll(nodesFixedOrNot)
            isBFSStarterSelected=0
            startingBFSNode=0
            stateOfConnection=0
        }


    }

    private suspend fun bfs(u:Int) {
        val queue: Queue<Int> = LinkedList<Int>()
        queue.add(u)
        checker[u]=1
        while (!queue.isEmpty())
        {
            var front=queue.peek()
            queue.remove()
            //nodes[front].setImageDrawable(resources.getDrawable(R.drawable.ic_circle))

            for (j in 0..links[front].size-1)
            {
                if(checker[links[front][j]]==0)
                {
                    nodes[links[front][j]].setImageDrawable(resources.getDrawable(R.drawable.ic_circle))
                    delay(1000)
                    queue.add(links[front][j])
                    checker[links[front][j]]=1

                }
            }
        }
        isBFSStarterSelected=0
        getMode=0
        visualize.isClickable=false
        btnstarting_point.setBackgroundColor(Color.WHITE)
    }

    private fun addTV() {
        val ivNode= ImageView(this)
        ivNode.setImageDrawable(resources.getDrawable(R.drawable.ic_circle_vol_1circle))
        val layoutParamsForivNode = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParamsForivNode.width = 100;
        layoutParamsForivNode.height = 100;
        ivNode.setLayoutParams(layoutParamsForivNode)
        rlCanvas.addView(ivNode)
        noOfNodes++
        ivNode.setOnTouchListener(this)
        ivNode.x=50f
        ivNode.y=50f
        nodes.add(ivNode)
        nodesFixedOrNot.add(0)
        var connectionsOfOneNode= mutableListOf<LineView>()
        if(noOfNodes>0) {
            for (i in 0 until connections.size) {
                connections[i].add(fakeLineView)
            }
            for (i in 0..noOfNodes) {
                connectionsOfOneNode.add(fakeLineView)
            }
            connections.add(connectionsOfOneNode)
        }
        ivNode.setOnLongClickListener {
            if(getMode==0) {
                return@setOnLongClickListener false
            }
            GlobalScope.launch(Dispatchers.IO) {
                val vibratorForLongClick = this@MainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorForLongClick.vibrate(100)
            }
            if(getMode==3){
                if(isBFSStarterSelected==0) {
                    ivNode.setImageDrawable(resources.getDrawable(R.drawable.ic_add))
                    startingBFSNode = nodes.indexOf(ivNode)
                    isBFSStarterSelected = 1
                }
                return@setOnLongClickListener true
            }
            if(stateOfConnection==0){
                pointAForLine= PointF(ivNode.x+(ivNode.width/2),ivNode.y+(ivNode.height/2))
                stateOfConnection=1
                lastNode=ivNode
            }
            else{
                if(getMode==1) {
                    pointBForLine = PointF(ivNode.x + (ivNode.width / 2), ivNode.y + (ivNode.height / 2))
                    var lvConnection = LineView(this)
                    rlCanvas.addView(lvConnection)
                    lvConnection.pointA = pointAForLine
                    lvConnection.pointB = pointBForLine
                    lvConnection.draw()
                    stateOfConnection = 0
                    getMode = 0
                    btnConnection.setBackgroundColor(Color.WHITE)
                    var indexOfCurrentNode = nodes.indexOf(ivNode)
                    nodesFixedOrNot[indexOfCurrentNode] = 1
                    var indexOfLastNode = nodes.indexOf(lastNode)
                    nodesFixedOrNot[indexOfLastNode] = 1
                    connections[indexOfCurrentNode][indexOfLastNode]=lvConnection
                    connections[indexOfLastNode][indexOfCurrentNode]=lvConnection
                }
                else if(getMode==2){
                    var indexOfLastNode = nodes.indexOf(lastNode)
                    var indexOfCurrentNode = nodes.indexOf(ivNode)
                    if(connections[indexOfCurrentNode][indexOfLastNode]!=fakeLineView || connections[indexOfLastNode][indexOfCurrentNode]!=fakeLineView) {
                        rlCanvas.removeView(connections[indexOfCurrentNode][indexOfLastNode])
                        connections[indexOfCurrentNode][indexOfLastNode] = fakeLineView
                        connections[indexOfLastNode][indexOfCurrentNode] = fakeLineView
                        btnRemoveConnection.setBackgroundColor(Color.WHITE)
                        getMode = 0
                        stateOfConnection = 0
                        var isCurrentNodeFree = 1
                        for (i in 0..connections[indexOfCurrentNode].size - 1) {
                            if (connections[indexOfCurrentNode][i] != fakeLineView) {
                                isCurrentNodeFree = 0
                            }
                        }
                        if (isCurrentNodeFree == 1)
                            nodesFixedOrNot[indexOfCurrentNode] = 0
                        var isLastNodeFree = 1
                        for (i in 0..connections[indexOfLastNode].size - 1) {
                            if (connections[indexOfLastNode][i] != fakeLineView) {
                                isLastNodeFree = 0
                            }
                        }
                        if (isLastNodeFree == 1)
                            nodesFixedOrNot[indexOfLastNode] = 0
                    }
                    else{
                        Toast.makeText(this,"NO CONNECTION TO REMOVE", Toast.LENGTH_SHORT).show()
                        getMode=0
                        stateOfConnection=0
                        btnRemoveConnection.setBackgroundColor(Color.WHITE)
                    }
                }
            }
            true
        }
    }

    override fun onDrag(view: View, dragEvent: DragEvent):Boolean {
        Log.d(TAG, "onDrag: view->$view\n DragEvent$dragEvent")
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d(TAG, "onDrag: ACTION_DRAG_ENDED ")
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d(TAG, "onDrag: ACTION_DRAG_EXITED")
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d(TAG, "onDrag: ACTION_DRAG_ENTERED")
                return true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d(TAG, "onDrag: ACTION_DRAG_STARTED")
                val tvState = dragEvent.localState as View

                return true
            }
            DragEvent.ACTION_DROP -> {
                Log.d(TAG, "onDrag: ACTION_DROP")
                Log.d(TAG, "onDrag:viewX" + dragEvent.x + "viewY" + dragEvent.y)

                //container.visibility = View.VISIBLE
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                Log.d(TAG, "onDrag: ACTION_DRAG_LOCATION")
                val tvState = dragEvent.localState as View
                if(dragEvent.x - (tvState.width / 2)>rlCanvas.width-tvState.width)
                    tvState.x=rlCanvas.width-tvState.width-0f
                else if (dragEvent.x - (tvState.width / 2)>rlCanvas.width+tvState.width)
                    tvState.x=rlCanvas.width+tvState.width+0f
                else
                    tvState.x = dragEvent.x - (tvState.width / 2)
                if(dragEvent.y - (tvState.height / 2)>rlCanvas.height-tvState.height)
                    tvState.y=rlCanvas.height-tvState.height-0f
                else if (dragEvent.y - (tvState.height / 2)>rlCanvas.height+tvState.height)
                    tvState.y=rlCanvas.height+tvState.height+0f
                else
                    tvState.y = dragEvent.y - (tvState.height / 2)
                lastNodePosition= PointF(dragEvent.x-(tvState.width/2),dragEvent.y-(tvState.height/2))
                val tvParent = tvState.parent as ViewGroup
                tvParent.removeView(tvState)
                val container = view as RelativeLayout
                container.addView(tvState)
                return true
            }
            else -> return false
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent):Boolean {
        Log.d(TAG, "onTouch: view->view$view\n MotionEvent$motionEvent")
        if (getMode!=0){
            return false
        }
        var indexOfNode=nodes.indexOf(view)
        if(nodesFixedOrNot[indexOfNode]==1){
            return false
        }
        return if (motionEvent.action === MotionEvent.ACTION_DOWN) {
            val dragShadowBuilder = View.DragShadowBuilder(view)
            view.startDrag(null, dragShadowBuilder, view, 0)
            true
        } else {
            false
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        algorithm = pos
    }

}
