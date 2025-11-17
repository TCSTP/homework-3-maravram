package tcs.app.dev.homework1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Cart
import tcs.app.dev.homework1.data.Discount
import tcs.app.dev.homework1.data.Euro
import tcs.app.dev.homework1.data.Item
import tcs.app.dev.homework1.data.MockData
import tcs.app.dev.homework1.data.minus
import tcs.app.dev.homework1.data.plus
import tcs.app.dev.homework1.data.times

@Composable
fun CartContent(
    cart: Cart,
    modifier: Modifier = Modifier,
    onIncreaseItem: (Item) -> Unit,
    onDecreaseItem: (Item) -> Unit,
    onRemoveDiscount: (Discount) -> Unit
) {
    val itemsSubtotal = cart.itemsSubtotal()
    val discountTotal = itemsSubtotal - cart.price

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (cart.items.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.cart_items_section_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(
                items = cart.items.entries.toList(),
                key = { it.key.id }
            ) { (item, amount) ->
                val price = cart.shop.prices[item] ?: return@items
                CartItemRow(
                    item = item,
                    amount = amount,
                    price = price,
                    onIncrease = { onIncreaseItem(item) },
                    onDecrease = { onDecreaseItem(item) }
                )
            }
        }

        if (cart.discounts.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.label_discounts),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(
                items = cart.discounts,
                key = { discount -> discount.hashCode() }
            ) { discount ->
                AssistChip(
                    onClick = { onRemoveDiscount(discount) },
                    label = {
                        Text(text = discountLabel(discount))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.description_remove_from_cart)
                        )
                    }
                )
            }
        }

        item {
            CartSummary(
                itemsSubtotal = itemsSubtotal,
                discountTotal = discountTotal
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: Item,
    amount: UInt,
    price: Euro,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPrice = price * amount
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = MockData.getImage(item)),
                contentDescription = stringResource(id = MockData.getName(item)),
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = MockData.getName(item)),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = totalPrice.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            QuantitySelector(
                amount = amount,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    amount: UInt,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onDecrease,
            enabled = amount > 0u
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = stringResource(id = R.string.description_decrease_amount)
            )
        }
        Text(
            text = amount.toString(),
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(
            onClick = onIncrease,
            enabled = amount < 99u
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.description_increase_amount)
            )
        }
    }
}

@Composable
private fun CartSummary(
    itemsSubtotal: Euro,
    discountTotal: Euro,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.cart_items_subtotal, itemsSubtotal.toString()),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(id = R.string.cart_discounts_total, discountTotal.toString()),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun Cart.itemsSubtotal(): Euro =
    items
        .mapNotNull { (item, amount) ->
            shop.prices[item]?.let { price -> price * amount }
        }
        .fold(Euro(0u)) { acc, subtotal -> acc + subtotal }

@Composable
private fun discountLabel(discount: Discount): String = when (discount) {
    is Discount.Fixed -> stringResource(id = R.string.amount_off, discount.amount.toString())
    is Discount.Percentage -> stringResource(
        id = R.string.percentage_off,
        discount.value.toString()
    )

    is Discount.Bundle -> stringResource(
        id = R.string.pay_n_items_and_get,
        discount.amountItemsPay.toString(),
        stringResource(id = MockData.getName(discount.item)),
        discount.amountItemsGet.toString()
    )
}

