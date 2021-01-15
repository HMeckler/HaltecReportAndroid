package com.example.haltecreport

//Object used to store the information for a single wrench, there are two of each wrench type.
class WrenchData {
    var SerialNumber: String? = null //Serial number of the wrench
    var ReadingOne: String? = null //The first reading
    var ReadingTwo: String? = null //The second reading
    var InTolerance: Boolean = true //Boolean as to whether or not the wrench was within tolerance (+/- 4%)
    var NeedRepairs: Boolean = true //Boolean as to whether or not the wrench needs additional repairs
}