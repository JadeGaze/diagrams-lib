package com.example.graphiclib.data

import com.example.graphiclib.ui.base.TreeNode


data class BarNode(
    override val id: String,
    override val label: String,
    val value: Float,
    override val children: List<BarNode> = emptyList(),
) : TreeNode<BarNode>