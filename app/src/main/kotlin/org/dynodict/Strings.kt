package org.dynodict

import org.dynodict.model.StringKey

object App : StringKey("App") {
    object Name : StringKey("Name", App) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
}
object Title : StringKey("Title") {
    object Home : StringKey("Home", Title) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Add : StringKey("Add", Title) {
        object Group : StringKey("Group", Add) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
        object Quote : StringKey("Quote", Add) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
    object Edit : StringKey("Edit", Title) {
        object Group : StringKey("Group", Edit) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
        object Quote : StringKey("Quote", Edit) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
    object Settings : StringKey("Settings", Title) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
}
object Label : StringKey("Label") {
    object Add : StringKey("Add", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Edit : StringKey("Edit", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Cancel : StringKey("Cancel", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Group : StringKey("Group", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Description : StringKey("Description", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Delete : StringKey("Delete", Label) {
        object Group2 : StringKey("Group", Delete) {
            object Message : StringKey("Message", Group) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
        object Group : StringKey("Group", Delete) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
        object Quote : StringKey("Quote", Delete) {
            object Message : StringKey("Message", Quote) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
        object Quote2 : StringKey("Quote", Delete) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
    object Quote : StringKey("Quote", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Author : StringKey("Author", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Empty : StringKey("Empty", Label) {
        object State : StringKey("State", Empty) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
    object Sign : StringKey("Sign", Label) {
        object In : StringKey("In", Sign) {
            object Success : StringKey("Success", In) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
            object Error : StringKey("Error", In) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
        object Out : StringKey("Out", Sign) {
            object Success : StringKey("Success", Out) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
    }
    object User : StringKey("User", Label) {
        object Signed : StringKey("Signed", User) {
            object In : StringKey("In", Signed) {
                object As : StringKey("As", In) {
                    fun get(): String {
                        return DynoDict.instance.get(this)
                    }
                }
            }
        }
        object Not : StringKey("Not", User) {
            object Registered : StringKey("Registered", Not) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
    }
    object Apply : StringKey("Apply", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Frequency : StringKey("Frequency", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
    object Applied : StringKey("Applied", Label) {
        fun get(): String {
            return DynoDict.instance.get(this)
        }
    }
}
object Sign : StringKey("Sign") {
    object In : StringKey("In", Sign) {
        object Alert : StringKey("Alert", In) {
            object Dialog : StringKey("Dialog", Alert) {
                object Text : StringKey("Text", Dialog) {
                    fun get(): String {
                        return DynoDict.instance.get(this)
                    }
                }
            }
        }
    }
}
object Once : StringKey("Once") {
    object A : StringKey("A", Once) {
        object Week : StringKey("Week", A) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
        object Day : StringKey("Day", A) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
    object In : StringKey("In", Once) {
        object A : StringKey("A", In) {
            object Five : StringKey("Five", A) {
                object Days : StringKey("Days", Five) {
                    fun get(): String {
                        return DynoDict.instance.get(this)
                    }
                }
            }
        }
        object Two : StringKey("Two", In) {
            object Days : StringKey("Days", Two) {
                fun get(): String {
                    return DynoDict.instance.get(this)
                }
            }
        }
    }
}
object Twice : StringKey("Twice") {
    object A : StringKey("A", Twice) {
        object Day : StringKey("Day", A) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
}
object Fours : StringKey("Fours") {
    object A : StringKey("A", Fours) {
        object Day : StringKey("Day", A) {
            fun get(): String {
                return DynoDict.instance.get(this)
            }
        }
    }
}
