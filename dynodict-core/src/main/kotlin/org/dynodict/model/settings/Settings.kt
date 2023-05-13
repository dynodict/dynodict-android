package org.dynodict.model.settings

data class Settings(
    val stringNotFoundPolicy: StringNotFoundPolicy,
    val redundantPlaceholderPolicy: RedundantPlaceholderPolicy,
    val notFoundPlaceholderPolicy: NotFoundPlaceholderPolicy,
    val ignoreUnknownKeys: Boolean
) {
    companion object {

        val Strict = Settings(
            stringNotFoundPolicy = StringNotFoundPolicy.ThrowException,
            redundantPlaceholderPolicy = RedundantPlaceholderPolicy.ThrowException,
            notFoundPlaceholderPolicy = NotFoundPlaceholderPolicy.ThrowException,
            ignoreUnknownKeys = false,
        )

        val Default = Strict

        val Production = Settings(
            stringNotFoundPolicy = StringNotFoundPolicy.ReturnDefault,
            redundantPlaceholderPolicy = RedundantPlaceholderPolicy.Remove,
            notFoundPlaceholderPolicy = NotFoundPlaceholderPolicy.Nothing,
            ignoreUnknownKeys = true,
        )
    }
}

/**
 * Policy which should be applied when there are too many parameters
 * passed to the Dynodict i.e.
 * Example:
 * String Price.TotalPrice.Label: "Total price is {totalPrice} {redundantParam}"
 * Usage: val priceText = Price.TotalPrice.Label.get(price)
 */
enum class RedundantPlaceholderPolicy {
    ThrowException, // throw exception when too many placeholder are in the text
    Nothing, // do nothing with input
    Remove // Remove placeholder from the input
}

/**
 * Policy which should be applied when there are too few parameters
 * in text received from the server
 * Example:
 * String Id: User.Data.MoneyToPayLabel
 * Original: "You have to pay {price} till {date}"
 * From Server: "You have to pay {price}."
 * Usage: val payText = User.Data.MoneyToPayLabel.get(price, dueDate)
 */
enum class NotFoundPlaceholderPolicy {
    ThrowException, Nothing
}

