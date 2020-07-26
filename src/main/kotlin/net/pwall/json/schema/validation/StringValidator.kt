/*
 * @(#) StringValidator.kt
 *
 * json-kotlin-schema Kotlin implementation of JSON Schema
 * Copyright (c) 2020 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.schema.validation

import net.pwall.json.JSONString
import net.pwall.json.JSONValue
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.output.Output
import java.net.URI

class StringValidator(uri: URI?, location: JSONPointer, private val condition: ValidationType, private val value: Int) :
        JSONSchema.Validator(uri, location) {

    enum class ValidationType(val keyword: String) {
        MAX_LENGTH("maxLength"),
        MIN_LENGTH("minLength")
    }

    override fun childLocation(pointer: JSONPointer): JSONPointer = pointer.child(condition.keyword)

    override fun validate(relativeLocation: JSONPointer, json: JSONValue?, instanceLocation: JSONPointer): Output {
        val instance = instanceLocation.eval(json)
        if (instance !is JSONString)
            return trueOutput
        val result: Boolean = when (condition) {
            ValidationType.MAX_LENGTH -> instance.length <= value
            ValidationType.MIN_LENGTH -> instance.length >= value
        }
        return if (result) trueOutput else createError(relativeLocation, instanceLocation,
                "String fails length check: ${condition.keyword} $value, was ${instance.length}")
    }

    override fun equals(other: Any?): Boolean =
            this === other || other is StringValidator && super.equals(other) && condition == other.condition &&
                    value == other.value

    override fun hashCode(): Int = super.hashCode() xor condition.hashCode() xor value.hashCode()

}
