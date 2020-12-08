package com.example.withpresso.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.withpresso.R
import kotlinx.android.synthetic.main.children_list.view.*
import kotlinx.android.synthetic.main.parent_list.view.*

class ExpandableListAdapter(
    private val context: Context,
    private val parentList: ArrayList<String>,
    private val childrenList: ArrayList<ArrayList<String>>): BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int) = parentList.size

    override fun getChild(groupPosition: Int, childPosition: Int) = childrenList[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildrenCount(groupPosition: Int) = childrenList[groupPosition].size

    override fun getGroupCount() = parentList.size

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    override fun hasStableIds(): Boolean  = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val cell = LayoutInflater.from(context).inflate(R.layout.parent_list, parent, false)
        cell.info_type_text.text = parentList[groupPosition]

        return cell
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val cell = LayoutInflater.from(context).inflate(R.layout.children_list, parent, false)
        cell.info_question_text.text = childrenList[groupPosition][childPosition]

        return cell
    }

}