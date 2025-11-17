package tcs.app.dev.homework1

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Cart
import tcs.app.dev.homework1.data.Discount
import tcs.app.dev.homework1.data.Item
import tcs.app.dev.homework1.data.Shop
import tcs.app.dev.homework1.data.minus
import tcs.app.dev.homework1.data.plus

/**
 * # Homework 3 — Shop App
 *
 * Build a small shopping UI with ComposeUI using the **example data** from the
 * `tcs.app.dev.homework.data` package (items, prices, discounts, and ui resources).
 * The goal is to implement three tabs: **Shop**, **Discounts**, and **Cart**.
 *
 * ## Entry point
 *
 * The composable function [ShopScreen] is your entry point that holds the UI state
 * (selected tab and the current `Cart`).
 *
 * ## Data
 *
 * - Use the provided **example data** and data types from the `data` package:
 *   - `Shop`, `Item`, `Discount`, `Cart`, and `Euro`.
 *   - There are useful resources in `res/drawable` and `res/values/strings.xml`.
 *     You can add additional ones.
 *     Do **not** hard-code strings in the UI!
 *
 * ## Requirements
 *
 * 1) **Shop item tab**
 *    - Show all items offered by the shop, each row displaying:
 *      - item image + name,
 *      - item price,
 *      - an *Add to cart* button.
 *    - Tapping *Add to cart* increases the count of that item in the cart by 1.
 *
 * 2) **Discount tab**
 *    - Show all available discounts with:
 *      - an icon + text describing the discount,
 *      - an *Add to cart* button.
 *    - **Constraint:** each discount can be added **at most once**.
 *      Disable the button (or ignore clicks) for discounts already in the cart.
 *
 * 3) **Cart tab**
 *    - Only show the **Cart** tab contents if the cart is **not empty**. Within the cart:
 *      - List each cart item with:
 *        - image + name,
 *        - per-row total (`price * amount`),
 *        - an amount selector to **increase/decrease** the quantity (min 0, sensible max like 99).
 *      - Show all selected discounts with a way to **remove** them from the cart.
 *      - At the bottom, show:
 *        - the **total price** of the cart (items minus discounts),
 *        - a **Pay** button that is enabled only when there is at least one item in the cart.
 *      - When **Pay** is pressed, **simulate payment** by clearing the cart and returning to the
 *        **Shop** tab.
 *
 * ## Navigation
 * - **Top bar**:
 *      - Title shows either the shop name or "Cart".
 *      - When not in Cart, show a cart icon.
 *        If you feel fancy you can add a badge to the icon showing the total count (capped e.g. at "99+").
 *      - The cart button is enabled only if the cart contains items. In the Cart screen, show a back
 *        button to return to the shop.
 *
 * - **Bottom bar**:
 *       - In Shop/Discounts, show a 2-tab bottom bar to switch between **Shop** and **Discounts**.
 *       - In Cart, hide the tab bar and instead show the cart bottom bar with the total and **Pay**
 *         action as described above.
 *
 * ## Hints
 * - Keep your cart as a single source of truth and derive counts/price from it.
 *   Rendering each list can be done with a `LazyColumn` and stable keys (`item.id`, discount identity).
 * - Provide small reusable row components for items, cart rows, and discount rows.
 *   This keeps the screen implementation compact.
 *
 * ## Bonus (optional)
 * Make the app feel polished with simple animations, such as:
 * - `AnimatedVisibility` for showing/hiding the cart,
 * - `animateContentSize()` on rows when amounts change,
 * - transitions when switching tabs or updating the cart badge.
 *
 * These can help if want you make the app feel polished:
 * - [NavigationBar](https://developer.android.com/develop/ui/compose/components/navigation-bar)
 * - [Card](https://developer.android.com/develop/ui/compose/components/card)
 * - [Swipe to dismiss](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/swipe-to-dismiss)
 * - [App bars](https://developer.android.com/develop/ui/compose/components/app-bars#top-bar)
 * - [Pager](https://developer.android.com/develop/ui/compose/layouts/pager)
 *
 */
@Composable
fun ShopScreen(shop: Shop, availableDiscounts: List<Discount>, modifier: Modifier = Modifier) {
    var cart by rememberSaveable { mutableStateOf(Cart(shop = shop)) }
    var destination by rememberSaveable { mutableStateOf(ShopDestination.Shop) }
    val canOpenCart = cart.itemCount > 0u

    LaunchedEffect(canOpenCart, destination) {
        if (destination == ShopDestination.Cart && !canOpenCart) {
            destination = ShopDestination.Shop
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ShopTopBar(
                title =
                    when (destination) {
                        ShopDestination.Shop -> stringResource(id = R.string.name_shop)
                        ShopDestination.Discounts ->
                            stringResource(id = R.string.title_discounts)

                        ShopDestination.Cart -> stringResource(id = R.string.title_cart)
                    },
                destination = destination,
                cartCount = cart.totalCount,
                canOpenCart = canOpenCart,
                onBackClick = { destination = ShopDestination.Shop },
                onCartClick = { destination = ShopDestination.Cart }
            )
        },
        bottomBar = {
            if (destination == ShopDestination.Cart && canOpenCart) {
                CartBottomBar(
                    total = cart.price,
                    payEnabled = cart.itemCount > 0u,
                    onPay = {
                        cart = Cart(shop = shop)
                        destination = ShopDestination.Shop
                    }
                )
            } else {
                ShopNavigationBar(
                    selected = destination,
                    onSelected = { selected -> destination = selected }
                )
            }
        }
    ) { padding ->
        when {
            destination == ShopDestination.Cart && canOpenCart -> {
                CartContent(
                    cart = cart,
                    modifier = Modifier.padding(padding),
                    onIncreaseItem = { item -> cart = cart.incrementItem(item) },
                    onDecreaseItem = { item -> cart = cart.decrementItem(item) },
                    onRemoveDiscount = { discount -> cart = cart.removeDiscount(discount) }
                )
            }

            destination == ShopDestination.Discounts -> {
                DiscountList(
                    discounts = availableDiscounts,
                    cart = cart,
                    modifier = Modifier.padding(padding),
                    onAddDiscount = { discount ->
                        if (discount !in cart.discounts) {
                            cart = cart.plus(discount)
                        }
                    }
                )
            }

            else -> {
                StoreItemList(
                    shop = shop,
                    modifier = Modifier.padding(padding),
                    onAddItem = { item -> cart = cart + item }
                )
            }
        }
    }
}

enum class ShopDestination {
    Shop,
    Discounts,
    Cart
}

private fun Cart.incrementItem(item: Item): Cart {
    val current = items[item] ?: 0u
    val next = (current + 1u).coerceAtMost(99u)
    return copy(items = items + (item to next))
}

private fun Cart.decrementItem(item: Item): Cart {
    val current = items[item] ?: return this
    val next = if (current == 0u) 0u else current - 1u
    return if (next == 0u) {
        this.minus(item)
    } else {
        copy(items = items + (item to next))
    }
}

private fun Cart.removeDiscount(discount: Discount): Cart = this - discount