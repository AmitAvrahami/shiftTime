package com.example.shiftime.logic

import com.example.shiftime.data.ScoringWeights

abstract class BaseShiftAssigner(
    protected val weights: ScoringWeights
) : ShiftAssigner {


}