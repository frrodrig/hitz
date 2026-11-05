"""Course illustrations, one module per family of pictures. ``load()`` imports them all."""
from __future__ import annotations

import importlib
import pkgutil

from .kit import REGISTRY, Drawing


def load() -> dict[str, tuple[str, Drawing]]:
    for module in pkgutil.iter_modules(__path__):
        if module.name != "kit":
            importlib.import_module(f"{__name__}.{module.name}")
    return REGISTRY
