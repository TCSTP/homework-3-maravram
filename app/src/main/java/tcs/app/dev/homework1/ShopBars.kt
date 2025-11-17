package tcs.app.dev.homework1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Euro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopTopBar(
    title: String,
    destination: ShopDestination,
    cartCount: UInt,
    canOpenCart: Boolean,
    onCartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
            )
        },
        navigationIcon = {
            if (destination == ShopDestination.Cart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.description_go_to_shop)
                    )
                }
            }
        },
        actions = {
            if (destination != ShopDestination.Cart) {
                IconButton(
                    onClick = onCartClick,
                    enabled = canOpenCart
                ) {
                    if (cartCount > 0u) {
                        val badgeText = if (cartCount > 99u) {
                            stringResource(id = R.string.more_than_99)
                        } else {
                            cartCount.toString()
                        }
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(text = badgeText)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = stringResource(id = R.string.description_go_to_cart)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = stringResource(id = R.string.description_go_to_cart)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ShopNavigationBar(
    selected: ShopDestination,
    onSelected: (ShopDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = selected == ShopDestination.Shop,
            onClick = { onSelected(ShopDestination.Shop) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = stringResource(R.string.label_shop)
                )
            },
            label = { Text(text = stringResource(id = R.string.label_shop)) }
        )
        NavigationBarItem(
            selected = selected == ShopDestination.Discounts,
            onClick = { onSelected(ShopDestination.Discounts) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.LocalOffer,
                    contentDescription = stringResource(R.string.label_discounts)
                )
            },
            label = { Text(text = stringResource(id = R.string.label_discounts)) }
        )
    }
}

@Composable
fun CartBottomBar(
    total: Euro,
    payEnabled: Boolean,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.total_price, total.toString()),
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = onPay,
                enabled = payEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.label_pay),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

