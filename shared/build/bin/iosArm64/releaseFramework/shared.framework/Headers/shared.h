#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedConfidenceEngine, SharedConfidenceLevel, SharedFillUp, SharedTrip, SharedKotlinx_datetimeInstant, SharedKotlinEnumCompanion, SharedKotlinEnum<E>, SharedKotlinArray<T>, SharedMoney, SharedFuelPriceUnit, SharedFuelPricePerUnit, SharedFuelType, SharedStationProvider, SharedStationId, SharedUnitSystem, SharedCachedFuelPrice, SharedCachedFuelStation, SharedCachedQueryCell, SharedCachedQueryStation, SharedCachedStationRoute, SharedFillUp_, SharedTankPilotDbQueries, SharedTankPilotDbCompanion, SharedKotlinUnit, SharedRuntimeTransacterTransaction, SharedKotlinThrowable, SharedRuntimeBaseTransacterImpl, SharedRuntimeTransacterImpl, SharedRuntimeQuery<__covariant RowType>, SharedTrip_, SharedVehicle, SharedKotlinx_coroutines_coreCoroutineDispatcher, SharedFuelEngine, SharedDrivingType, SharedFuelStation, SharedFuelRescueEngine, SharedFuelStationRecommendation, SharedKotlinPair<__covariant A, __covariant B>, SharedStationFuelPrice, SharedReachabilityStatus, SharedPriceFreshness, SharedObdParser, SharedConnectionStatus, SharedObdCapabilities, SharedTelemetryData, SharedTelemetryMetadata, SharedVehicle_, SharedKoin_coreModule, SharedKoin_coreKoinApplication, SharedKotlinx_datetimeInstantCompanion, SharedRuntimeAfterVersion, SharedRuntimeExecutableQuery<__covariant RowType>, SharedKotlinException, SharedKotlinRuntimeException, SharedKotlinIllegalStateException, SharedKotlinAbstractCoroutineContextElement, SharedKotlinx_coroutines_coreCoroutineDispatcherKey, SharedKoin_coreKoinDefinition<R>, SharedKoin_coreScope, SharedKoin_coreParametersHolder, SharedKoin_coreInstanceFactory<T>, SharedKoin_coreSingleInstanceFactory<T>, SharedKoin_coreScopeDSL, SharedKoin_coreKoinApplicationCompanion, SharedKoin_coreLogger, SharedKoin_coreLevel, SharedKoin_coreKoin, SharedKotlinByteArray, SharedKotlinAbstractCoroutineContextKey<B, E>, SharedKoin_coreLockable, SharedKotlinLazyThreadSafetyMode, SharedKoin_coreBeanDefinition<T>, SharedKoin_coreInstanceFactoryCompanion, SharedKoin_coreResolutionContext, SharedKoin_coreExtensionManager, SharedKoin_coreInstanceRegistry, SharedKoin_corePropertyRegistry, SharedKoin_coreScopeRegistry, SharedKotlinByteIterator, SharedKoin_coreKind, SharedKoin_coreCallbacks<T>, SharedKoin_coreScopeRegistryCompanion, SharedKotlinx_serialization_coreSerializersModule, SharedKotlinx_serialization_coreSerialKind, SharedKotlinNothing;

@protocol SharedKotlinComparable, SharedRuntimeSqlDriver, SharedAppClock, SharedRuntimeTransactionWithoutReturn, SharedRuntimeTransactionWithReturn, SharedRuntimeTransacterBase, SharedRuntimeTransacter, SharedTankPilotDb, SharedRuntimeSqlSchema, SharedKotlinx_coroutines_coreFlow, SharedFillUpRepository, SharedFuelStationProvider, SharedFuelStationRepository, SharedKotlinx_coroutines_coreStateFlow, SharedVehicleTelemetryProvider, SharedTripRepository, SharedVehicleRepository, SharedKotlinIterator, SharedRuntimeQueryListener, SharedRuntimeQueryResult, SharedRuntimeSqlPreparedStatement, SharedRuntimeSqlCursor, SharedRuntimeCloseable, SharedRuntimeTransactionCallbacks, SharedKotlinx_coroutines_coreFlowCollector, SharedKotlinCoroutineContextKey, SharedKotlinCoroutineContextElement, SharedKotlinCoroutineContext, SharedKotlinContinuation, SharedKotlinContinuationInterceptor, SharedKotlinx_coroutines_coreRunnable, SharedKotlinx_coroutines_coreSharedFlow, SharedKoin_coreQualifier, SharedKotlinx_datetimeDateTimeFormat, SharedKotlinx_serialization_coreKSerializer, SharedKotlinKClass, SharedKotlinLazy, SharedKoin_coreScopeCallback, SharedKoin_coreKoinScopeComponent, SharedKotlinAppendable, SharedKotlinx_serialization_coreEncoder, SharedKotlinx_serialization_coreSerialDescriptor, SharedKotlinx_serialization_coreSerializationStrategy, SharedKotlinx_serialization_coreDecoder, SharedKotlinx_serialization_coreDeserializationStrategy, SharedKotlinKDeclarationContainer, SharedKotlinKAnnotatedElement, SharedKotlinKClassifier, SharedKoin_coreKoinComponent, SharedKoin_coreKoinExtension, SharedKotlinx_serialization_coreCompositeEncoder, SharedKotlinAnnotation, SharedKotlinx_serialization_coreCompositeDecoder, SharedKotlinx_serialization_coreSerializersModuleCollector;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface SharedBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface SharedBase (SharedBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface SharedMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface SharedMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorSharedKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface SharedNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface SharedByte : SharedNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface SharedUByte : SharedNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface SharedShort : SharedNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface SharedUShort : SharedNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface SharedInt : SharedNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface SharedUInt : SharedNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface SharedLong : SharedNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface SharedULong : SharedNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface SharedFloat : SharedNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface SharedDouble : SharedNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface SharedBoolean : SharedNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ConfidenceEngine")))
@interface SharedConfidenceEngine : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)confidenceEngine __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedConfidenceEngine *shared __attribute__((swift_name("shared")));
- (SharedConfidenceLevel *)calculateConfidenceFillUps:(NSArray<SharedFillUp *> *)fillUps trips:(NSArray<SharedTrip *> *)trips currentTimeMs:(int64_t)currentTimeMs __attribute__((swift_name("calculateConfidence(fillUps:trips:currentTimeMs:)")));
@end

__attribute__((swift_name("AppClock")))
@protocol SharedAppClock
@required
- (SharedKotlinx_datetimeInstant *)now __attribute__((swift_name("now()")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol SharedKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface SharedKotlinEnum<E> : SharedBase <SharedKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ConfidenceLevel")))
@interface SharedConfidenceLevel : SharedKotlinEnum<SharedConfidenceLevel *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedConfidenceLevel *veryHigh __attribute__((swift_name("veryHigh")));
@property (class, readonly) SharedConfidenceLevel *high __attribute__((swift_name("high")));
@property (class, readonly) SharedConfidenceLevel *medium __attribute__((swift_name("medium")));
@property (class, readonly) SharedConfidenceLevel *low __attribute__((swift_name("low")));
+ (SharedKotlinArray<SharedConfidenceLevel *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedConfidenceLevel *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DatabaseDriverFactory")))
@interface SharedDatabaseDriverFactory : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (id<SharedRuntimeSqlDriver>)createDriver __attribute__((swift_name("createDriver()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelPricePerUnit")))
@interface SharedFuelPricePerUnit : SharedBase
- (instancetype)initWithMoney:(SharedMoney *)money unit:(SharedFuelPriceUnit *)unit __attribute__((swift_name("init(money:unit:)"))) __attribute__((objc_designated_initializer));
- (SharedFuelPricePerUnit *)doCopyMoney:(SharedMoney *)money unit:(SharedFuelPriceUnit *)unit __attribute__((swift_name("doCopy(money:unit:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (SharedFuelPricePerUnit *)toPerGallon __attribute__((swift_name("toPerGallon()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedMoney *money __attribute__((swift_name("money")));
@property (readonly) SharedFuelPriceUnit *unit __attribute__((swift_name("unit")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelPriceUnit")))
@interface SharedFuelPriceUnit : SharedKotlinEnum<SharedFuelPriceUnit *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedFuelPriceUnit *perGallon __attribute__((swift_name("perGallon")));
@property (class, readonly) SharedFuelPriceUnit *perLiter __attribute__((swift_name("perLiter")));
+ (SharedKotlinArray<SharedFuelPriceUnit *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedFuelPriceUnit *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelType")))
@interface SharedFuelType : SharedKotlinEnum<SharedFuelType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedFuelType *regular __attribute__((swift_name("regular")));
@property (class, readonly) SharedFuelType *midgrade __attribute__((swift_name("midgrade")));
@property (class, readonly) SharedFuelType *premium __attribute__((swift_name("premium")));
@property (class, readonly) SharedFuelType *diesel __attribute__((swift_name("diesel")));
@property (class, readonly) SharedFuelType *e85 __attribute__((swift_name("e85")));
@property (class, readonly) SharedFuelType *unknown __attribute__((swift_name("unknown")));
+ (SharedKotlinArray<SharedFuelType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedFuelType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Money")))
@interface SharedMoney : SharedBase
- (instancetype)initWithAmountMicros:(int64_t)amountMicros currencyCode:(NSString *)currencyCode __attribute__((swift_name("init(amountMicros:currencyCode:)"))) __attribute__((objc_designated_initializer));
- (SharedMoney *)doCopyAmountMicros:(int64_t)amountMicros currencyCode:(NSString *)currencyCode __attribute__((swift_name("doCopy(amountMicros:currencyCode:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (SharedMoney *)minusOther:(SharedMoney *)other __attribute__((swift_name("minus(other:)")));
- (SharedMoney *)plusOther:(SharedMoney *)other __attribute__((swift_name("plus(other:)")));
- (SharedMoney *)timesFactor:(double)factor __attribute__((swift_name("times(factor:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t amountMicros __attribute__((swift_name("amountMicros")));
@property (readonly) NSString *currencyCode __attribute__((swift_name("currencyCode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StationId")))
@interface SharedStationId : SharedBase
- (instancetype)initWithProvider:(SharedStationProvider *)provider providerId:(NSString *)providerId __attribute__((swift_name("init(provider:providerId:)"))) __attribute__((objc_designated_initializer));
- (SharedStationId *)doCopyProvider:(SharedStationProvider *)provider providerId:(NSString *)providerId __attribute__((swift_name("doCopy(provider:providerId:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedStationProvider *provider __attribute__((swift_name("provider")));
@property (readonly) NSString *providerId __attribute__((swift_name("providerId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StationProvider")))
@interface SharedStationProvider : SharedKotlinEnum<SharedStationProvider *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedStationProvider *googlePlaces __attribute__((swift_name("googlePlaces")));
@property (class, readonly) SharedStationProvider *here __attribute__((swift_name("here")));
@property (class, readonly) SharedStationProvider *tomtom __attribute__((swift_name("tomtom")));
@property (class, readonly) SharedStationProvider *unknown __attribute__((swift_name("unknown")));
+ (SharedKotlinArray<SharedStationProvider *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedStationProvider *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SystemClock")))
@interface SharedSystemClock : SharedBase <SharedAppClock>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedKotlinx_datetimeInstant *)now __attribute__((swift_name("now()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UnitSystem")))
@interface SharedUnitSystem : SharedKotlinEnum<SharedUnitSystem *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedUnitSystem *imperial __attribute__((swift_name("imperial")));
@property (class, readonly) SharedUnitSystem *metric __attribute__((swift_name("metric")));
+ (SharedKotlinArray<SharedUnitSystem *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedUnitSystem *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CachedFuelPrice")))
@interface SharedCachedFuelPrice : SharedBase
- (instancetype)initWithStationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId fuelType:(NSString *)fuelType fuelGradeKey:(NSString *)fuelGradeKey displayFuelGrade:(NSString * _Nullable)displayFuelGrade priceAmountMicros:(int64_t)priceAmountMicros currencyCode:(NSString *)currencyCode priceUnit:(NSString *)priceUnit providerUpdatedAt:(SharedLong * _Nullable)providerUpdatedAt fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("init(stationProvider:stationProviderId:fuelType:fuelGradeKey:displayFuelGrade:priceAmountMicros:currencyCode:priceUnit:providerUpdatedAt:fetchedAt:)"))) __attribute__((objc_designated_initializer));
- (SharedCachedFuelPrice *)doCopyStationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId fuelType:(NSString *)fuelType fuelGradeKey:(NSString *)fuelGradeKey displayFuelGrade:(NSString * _Nullable)displayFuelGrade priceAmountMicros:(int64_t)priceAmountMicros currencyCode:(NSString *)currencyCode priceUnit:(NSString *)priceUnit providerUpdatedAt:(SharedLong * _Nullable)providerUpdatedAt fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("doCopy(stationProvider:stationProviderId:fuelType:fuelGradeKey:displayFuelGrade:priceAmountMicros:currencyCode:priceUnit:providerUpdatedAt:fetchedAt:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *currencyCode __attribute__((swift_name("currencyCode")));
@property (readonly) NSString * _Nullable displayFuelGrade __attribute__((swift_name("displayFuelGrade")));
@property (readonly) int64_t fetchedAt __attribute__((swift_name("fetchedAt")));
@property (readonly) NSString *fuelGradeKey __attribute__((swift_name("fuelGradeKey")));
@property (readonly) NSString *fuelType __attribute__((swift_name("fuelType")));
@property (readonly) int64_t priceAmountMicros __attribute__((swift_name("priceAmountMicros")));
@property (readonly) NSString *priceUnit __attribute__((swift_name("priceUnit")));
@property (readonly) SharedLong * _Nullable providerUpdatedAt __attribute__((swift_name("providerUpdatedAt")));
@property (readonly) NSString *stationProvider __attribute__((swift_name("stationProvider")));
@property (readonly) NSString *stationProviderId __attribute__((swift_name("stationProviderId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CachedFuelStation")))
@interface SharedCachedFuelStation : SharedBase
- (instancetype)initWithProvider:(NSString *)provider providerId:(NSString *)providerId name:(NSString *)name brand:(NSString * _Nullable)brand latitude:(double)latitude longitude:(double)longitude address:(NSString * _Nullable)address fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("init(provider:providerId:name:brand:latitude:longitude:address:fetchedAt:)"))) __attribute__((objc_designated_initializer));
- (SharedCachedFuelStation *)doCopyProvider:(NSString *)provider providerId:(NSString *)providerId name:(NSString *)name brand:(NSString * _Nullable)brand latitude:(double)latitude longitude:(double)longitude address:(NSString * _Nullable)address fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("doCopy(provider:providerId:name:brand:latitude:longitude:address:fetchedAt:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable address __attribute__((swift_name("address")));
@property (readonly) NSString * _Nullable brand __attribute__((swift_name("brand")));
@property (readonly) int64_t fetchedAt __attribute__((swift_name("fetchedAt")));
@property (readonly) double latitude __attribute__((swift_name("latitude")));
@property (readonly) double longitude __attribute__((swift_name("longitude")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) NSString *provider __attribute__((swift_name("provider")));
@property (readonly) NSString *providerId __attribute__((swift_name("providerId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CachedQueryCell")))
@interface SharedCachedQueryCell : SharedBase
- (instancetype)initWithId:(NSString *)id provider:(NSString *)provider latitudeCell:(int64_t)latitudeCell longitudeCell:(int64_t)longitudeCell radiusMiles:(double)radiusMiles fuelType:(NSString *)fuelType fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("init(id:provider:latitudeCell:longitudeCell:radiusMiles:fuelType:fetchedAt:expiresAt:)"))) __attribute__((objc_designated_initializer));
- (SharedCachedQueryCell *)doCopyId:(NSString *)id provider:(NSString *)provider latitudeCell:(int64_t)latitudeCell longitudeCell:(int64_t)longitudeCell radiusMiles:(double)radiusMiles fuelType:(NSString *)fuelType fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("doCopy(id:provider:latitudeCell:longitudeCell:radiusMiles:fuelType:fetchedAt:expiresAt:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t expiresAt __attribute__((swift_name("expiresAt")));
@property (readonly) int64_t fetchedAt __attribute__((swift_name("fetchedAt")));
@property (readonly) NSString *fuelType __attribute__((swift_name("fuelType")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) int64_t latitudeCell __attribute__((swift_name("latitudeCell")));
@property (readonly) int64_t longitudeCell __attribute__((swift_name("longitudeCell")));
@property (readonly) NSString *provider __attribute__((swift_name("provider")));
@property (readonly) double radiusMiles __attribute__((swift_name("radiusMiles")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CachedQueryStation")))
@interface SharedCachedQueryStation : SharedBase
- (instancetype)initWithQueryCellId:(NSString *)queryCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId __attribute__((swift_name("init(queryCellId:stationProvider:stationProviderId:)"))) __attribute__((objc_designated_initializer));
- (SharedCachedQueryStation *)doCopyQueryCellId:(NSString *)queryCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId __attribute__((swift_name("doCopy(queryCellId:stationProvider:stationProviderId:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *queryCellId __attribute__((swift_name("queryCellId")));
@property (readonly) NSString *stationProvider __attribute__((swift_name("stationProvider")));
@property (readonly) NSString *stationProviderId __attribute__((swift_name("stationProviderId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CachedStationRoute")))
@interface SharedCachedStationRoute : SharedBase
- (instancetype)initWithOriginCellId:(NSString *)originCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId routeMode:(NSString *)routeMode distanceMiles:(double)distanceMiles durationSeconds:(int64_t)durationSeconds fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("init(originCellId:stationProvider:stationProviderId:routeMode:distanceMiles:durationSeconds:fetchedAt:expiresAt:)"))) __attribute__((objc_designated_initializer));
- (SharedCachedStationRoute *)doCopyOriginCellId:(NSString *)originCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId routeMode:(NSString *)routeMode distanceMiles:(double)distanceMiles durationSeconds:(int64_t)durationSeconds fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("doCopy(originCellId:stationProvider:stationProviderId:routeMode:distanceMiles:durationSeconds:fetchedAt:expiresAt:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) double distanceMiles __attribute__((swift_name("distanceMiles")));
@property (readonly) int64_t durationSeconds __attribute__((swift_name("durationSeconds")));
@property (readonly) int64_t expiresAt __attribute__((swift_name("expiresAt")));
@property (readonly) int64_t fetchedAt __attribute__((swift_name("fetchedAt")));
@property (readonly) NSString *originCellId __attribute__((swift_name("originCellId")));
@property (readonly) NSString *routeMode __attribute__((swift_name("routeMode")));
@property (readonly) NSString *stationProvider __attribute__((swift_name("stationProvider")));
@property (readonly) NSString *stationProviderId __attribute__((swift_name("stationProviderId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FillUp_")))
@interface SharedFillUp_ : SharedBase
- (instancetype)initWithId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp gallonsAdded:(double)gallonsAdded price:(double)price odometer:(SharedDouble * _Nullable)odometer isFull:(int64_t)isFull __attribute__((swift_name("init(id:vehicleId:timestamp:gallonsAdded:price:odometer:isFull:)"))) __attribute__((objc_designated_initializer));
- (SharedFillUp_ *)doCopyId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp gallonsAdded:(double)gallonsAdded price:(double)price odometer:(SharedDouble * _Nullable)odometer isFull:(int64_t)isFull __attribute__((swift_name("doCopy(id:vehicleId:timestamp:gallonsAdded:price:odometer:isFull:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) double gallonsAdded __attribute__((swift_name("gallonsAdded")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) int64_t isFull __attribute__((swift_name("isFull")));
@property (readonly) SharedDouble * _Nullable odometer __attribute__((swift_name("odometer")));
@property (readonly) double price __attribute__((swift_name("price")));
@property (readonly) int64_t timestamp __attribute__((swift_name("timestamp")));
@property (readonly) NSString *vehicleId __attribute__((swift_name("vehicleId")));
@end

__attribute__((swift_name("RuntimeTransacterBase")))
@protocol SharedRuntimeTransacterBase
@required
@end

__attribute__((swift_name("RuntimeTransacter")))
@protocol SharedRuntimeTransacter <SharedRuntimeTransacterBase>
@required
- (void)transactionNoEnclosing:(BOOL)noEnclosing body:(void (^)(id<SharedRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(noEnclosing:body:)")));
- (id _Nullable)transactionWithResultNoEnclosing:(BOOL)noEnclosing bodyWithReturn:(id _Nullable (^)(id<SharedRuntimeTransactionWithReturn>))bodyWithReturn __attribute__((swift_name("transactionWithResult(noEnclosing:bodyWithReturn:)")));
@end

__attribute__((swift_name("TankPilotDb")))
@protocol SharedTankPilotDb <SharedRuntimeTransacter>
@required
@property (readonly) SharedTankPilotDbQueries *tankPilotDbQueries __attribute__((swift_name("tankPilotDbQueries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TankPilotDbCompanion")))
@interface SharedTankPilotDbCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedTankPilotDbCompanion *shared __attribute__((swift_name("shared")));
- (id<SharedTankPilotDb>)invokeDriver:(id<SharedRuntimeSqlDriver>)driver __attribute__((swift_name("invoke(driver:)")));
@property (readonly) id<SharedRuntimeSqlSchema> Schema __attribute__((swift_name("Schema")));
@end

__attribute__((swift_name("RuntimeBaseTransacterImpl")))
@interface SharedRuntimeBaseTransacterImpl : SharedBase
- (instancetype)initWithDriver:(id<SharedRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (NSString *)createArgumentsCount:(int32_t)count __attribute__((swift_name("createArguments(count:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)notifyQueriesIdentifier:(int32_t)identifier tableProvider:(void (^)(SharedKotlinUnit *(^)(NSString *)))tableProvider __attribute__((swift_name("notifyQueries(identifier:tableProvider:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (id _Nullable)postTransactionCleanupTransaction:(SharedRuntimeTransacterTransaction *)transaction enclosing:(SharedRuntimeTransacterTransaction * _Nullable)enclosing thrownException:(SharedKotlinThrowable * _Nullable)thrownException returnValue:(id _Nullable)returnValue __attribute__((swift_name("postTransactionCleanup(transaction:enclosing:thrownException:returnValue:)")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) id<SharedRuntimeSqlDriver> driver __attribute__((swift_name("driver")));
@end

__attribute__((swift_name("RuntimeTransacterImpl")))
@interface SharedRuntimeTransacterImpl : SharedRuntimeBaseTransacterImpl <SharedRuntimeTransacter>
- (instancetype)initWithDriver:(id<SharedRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));
- (void)transactionNoEnclosing:(BOOL)noEnclosing body:(void (^)(id<SharedRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(noEnclosing:body:)")));
- (id _Nullable)transactionWithResultNoEnclosing:(BOOL)noEnclosing bodyWithReturn:(id _Nullable (^)(id<SharedRuntimeTransactionWithReturn>))bodyWithReturn __attribute__((swift_name("transactionWithResult(noEnclosing:bodyWithReturn:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TankPilotDbQueries")))
@interface SharedTankPilotDbQueries : SharedRuntimeTransacterImpl
- (instancetype)initWithDriver:(id<SharedRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));
- (void)clearCache __attribute__((swift_name("clearCache()")));
- (void)clearExpiredRoutesExpiresAt:(int64_t)expiresAt __attribute__((swift_name("clearExpiredRoutes(expiresAt:)")));
- (void)clearQueryCells __attribute__((swift_name("clearQueryCells()")));
- (void)deleteExpiredQueryCellsExpiresAt:(int64_t)expiresAt __attribute__((swift_name("deleteExpiredQueryCells(expiresAt:)")));
- (void)deleteFillUpId:(NSString *)id __attribute__((swift_name("deleteFillUp(id:)")));
- (void)deleteTripId:(NSString *)id __attribute__((swift_name("deleteTrip(id:)")));
- (void)deleteVehicleId:(NSString *)id __attribute__((swift_name("deleteVehicle(id:)")));
- (SharedRuntimeQuery<SharedCachedFuelPrice *> *)getCachedPricesForStationStationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId __attribute__((swift_name("getCachedPricesForStation(stationProvider:stationProviderId:)")));
- (SharedRuntimeQuery<id> *)getCachedPricesForStationStationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId mapper:(id (^)(NSString *, NSString *, NSString *, NSString *, NSString * _Nullable, SharedLong *, NSString *, NSString *, SharedLong * _Nullable, SharedLong *))mapper __attribute__((swift_name("getCachedPricesForStation(stationProvider:stationProviderId:mapper:)")));
- (SharedRuntimeQuery<SharedCachedQueryCell *> *)getCachedQueryCellId:(NSString *)id __attribute__((swift_name("getCachedQueryCell(id:)")));
- (SharedRuntimeQuery<id> *)getCachedQueryCellId:(NSString *)id mapper:(id (^)(NSString *, NSString *, SharedLong *, SharedLong *, SharedDouble *, NSString *, SharedLong *, SharedLong *))mapper __attribute__((swift_name("getCachedQueryCell(id:mapper:)")));
- (SharedRuntimeQuery<SharedCachedStationRoute *> *)getCachedRouteOriginCellId:(NSString *)originCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId routeMode:(NSString *)routeMode __attribute__((swift_name("getCachedRoute(originCellId:stationProvider:stationProviderId:routeMode:)")));
- (SharedRuntimeQuery<id> *)getCachedRouteOriginCellId:(NSString *)originCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId routeMode:(NSString *)routeMode mapper:(id (^)(NSString *, NSString *, NSString *, NSString *, SharedDouble *, SharedLong *, SharedLong *, SharedLong *))mapper __attribute__((swift_name("getCachedRoute(originCellId:stationProvider:stationProviderId:routeMode:mapper:)")));
- (SharedRuntimeQuery<SharedCachedFuelStation *> *)getCachedStations __attribute__((swift_name("getCachedStations()")));
- (SharedRuntimeQuery<id> *)getCachedStationsMapper:(id (^)(NSString *, NSString *, NSString *, NSString * _Nullable, SharedDouble *, SharedDouble *, NSString * _Nullable, SharedLong *))mapper __attribute__((swift_name("getCachedStations(mapper:)")));
- (SharedRuntimeQuery<SharedCachedFuelStation *> *)getCachedStationsForQueryQueryCellId:(NSString *)queryCellId __attribute__((swift_name("getCachedStationsForQuery(queryCellId:)")));
- (SharedRuntimeQuery<id> *)getCachedStationsForQueryQueryCellId:(NSString *)queryCellId mapper:(id (^)(NSString *, NSString *, NSString *, NSString * _Nullable, SharedDouble *, SharedDouble *, NSString * _Nullable, SharedLong *))mapper __attribute__((swift_name("getCachedStationsForQuery(queryCellId:mapper:)")));
- (SharedRuntimeQuery<SharedFillUp_ *> *)getFillUpsForVehicleVehicleId:(NSString *)vehicleId __attribute__((swift_name("getFillUpsForVehicle(vehicleId:)")));
- (SharedRuntimeQuery<id> *)getFillUpsForVehicleVehicleId:(NSString *)vehicleId mapper:(id (^)(NSString *, NSString *, SharedLong *, SharedDouble *, SharedDouble *, SharedDouble * _Nullable, SharedLong *))mapper __attribute__((swift_name("getFillUpsForVehicle(vehicleId:mapper:)")));
- (SharedRuntimeQuery<SharedFillUp_ *> *)getRecentFillUpsForVehicleVehicleId:(NSString *)vehicleId value_:(int64_t)value_ __attribute__((swift_name("getRecentFillUpsForVehicle(vehicleId:value_:)")));
- (SharedRuntimeQuery<id> *)getRecentFillUpsForVehicleVehicleId:(NSString *)vehicleId value:(int64_t)value mapper:(id (^)(NSString *, NSString *, SharedLong *, SharedDouble *, SharedDouble *, SharedDouble * _Nullable, SharedLong *))mapper __attribute__((swift_name("getRecentFillUpsForVehicle(vehicleId:value:mapper:)")));
- (SharedRuntimeQuery<SharedTrip_ *> *)getRecentTripsForVehicleVehicleId:(NSString *)vehicleId value_:(int64_t)value_ __attribute__((swift_name("getRecentTripsForVehicle(vehicleId:value_:)")));
- (SharedRuntimeQuery<id> *)getRecentTripsForVehicleVehicleId:(NSString *)vehicleId value:(int64_t)value mapper:(id (^)(NSString *, NSString *, SharedLong *, SharedDouble *, SharedLong *, SharedLong *, SharedDouble *, NSString *, SharedDouble *))mapper __attribute__((swift_name("getRecentTripsForVehicle(vehicleId:value:mapper:)")));
- (SharedRuntimeQuery<SharedTrip_ *> *)getTripsForVehicleVehicleId:(NSString *)vehicleId __attribute__((swift_name("getTripsForVehicle(vehicleId:)")));
- (SharedRuntimeQuery<id> *)getTripsForVehicleVehicleId:(NSString *)vehicleId mapper:(id (^)(NSString *, NSString *, SharedLong *, SharedDouble *, SharedLong *, SharedLong *, SharedDouble *, NSString *, SharedDouble *))mapper __attribute__((swift_name("getTripsForVehicle(vehicleId:mapper:)")));
- (SharedRuntimeQuery<SharedVehicle *> *)getVehicleByIdId:(NSString *)id __attribute__((swift_name("getVehicleById(id:)")));
- (SharedRuntimeQuery<id> *)getVehicleByIdId:(NSString *)id mapper:(id (^)(NSString *, SharedLong *, NSString *, NSString *, NSString * _Nullable, NSString * _Nullable, NSString *, SharedDouble * _Nullable, SharedLong * _Nullable, SharedDouble *, SharedDouble *, SharedDouble *, SharedDouble *, NSString *, NSString * _Nullable, NSString *, SharedDouble *, SharedDouble *))mapper __attribute__((swift_name("getVehicleById(id:mapper:)")));
- (SharedRuntimeQuery<SharedVehicle *> *)getVehicles __attribute__((swift_name("getVehicles()")));
- (SharedRuntimeQuery<id> *)getVehiclesMapper:(id (^)(NSString *, SharedLong *, NSString *, NSString *, NSString * _Nullable, NSString * _Nullable, NSString *, SharedDouble * _Nullable, SharedLong * _Nullable, SharedDouble *, SharedDouble *, SharedDouble *, SharedDouble *, NSString *, NSString * _Nullable, NSString *, SharedDouble *, SharedDouble *))mapper __attribute__((swift_name("getVehicles(mapper:)")));
- (void)insertCachedPriceStationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId fuelType:(NSString *)fuelType fuelGradeKey:(NSString *)fuelGradeKey displayFuelGrade:(NSString * _Nullable)displayFuelGrade priceAmountMicros:(int64_t)priceAmountMicros currencyCode:(NSString *)currencyCode priceUnit:(NSString *)priceUnit providerUpdatedAt:(SharedLong * _Nullable)providerUpdatedAt fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("insertCachedPrice(stationProvider:stationProviderId:fuelType:fuelGradeKey:displayFuelGrade:priceAmountMicros:currencyCode:priceUnit:providerUpdatedAt:fetchedAt:)")));
- (void)insertCachedQueryCellId:(NSString *)id provider:(NSString *)provider latitudeCell:(int64_t)latitudeCell longitudeCell:(int64_t)longitudeCell radiusMiles:(double)radiusMiles fuelType:(NSString *)fuelType fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("insertCachedQueryCell(id:provider:latitudeCell:longitudeCell:radiusMiles:fuelType:fetchedAt:expiresAt:)")));
- (void)insertCachedQueryStationQueryCellId:(NSString *)queryCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId __attribute__((swift_name("insertCachedQueryStation(queryCellId:stationProvider:stationProviderId:)")));
- (void)insertCachedRouteOriginCellId:(NSString *)originCellId stationProvider:(NSString *)stationProvider stationProviderId:(NSString *)stationProviderId routeMode:(NSString *)routeMode distanceMiles:(double)distanceMiles durationSeconds:(int64_t)durationSeconds fetchedAt:(int64_t)fetchedAt expiresAt:(int64_t)expiresAt __attribute__((swift_name("insertCachedRoute(originCellId:stationProvider:stationProviderId:routeMode:distanceMiles:durationSeconds:fetchedAt:expiresAt:)")));
- (void)insertCachedStationProvider:(NSString *)provider providerId:(NSString *)providerId name:(NSString *)name brand:(NSString * _Nullable)brand latitude:(double)latitude longitude:(double)longitude address:(NSString * _Nullable)address fetchedAt:(int64_t)fetchedAt __attribute__((swift_name("insertCachedStation(provider:providerId:name:brand:latitude:longitude:address:fetchedAt:)")));
- (void)insertFillUpId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp gallonsAdded:(double)gallonsAdded price:(double)price odometer:(SharedDouble * _Nullable)odometer isFull:(int64_t)isFull __attribute__((swift_name("insertFillUp(id:vehicleId:timestamp:gallonsAdded:price:odometer:isFull:)")));
- (void)insertTripId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp distance:(double)distance duration:(int64_t)duration idleTime:(int64_t)idleTime averageSpeed:(double)averageSpeed drivingType:(NSString *)drivingType fuelBurned:(double)fuelBurned __attribute__((swift_name("insertTrip(id:vehicleId:timestamp:distance:duration:idleTime:averageSpeed:drivingType:fuelBurned:)")));
- (void)insertVehicleId:(NSString *)id year:(int64_t)year make:(NSString *)make model:(NSString *)model trim:(NSString * _Nullable)trim color:(NSString * _Nullable)color engine:(NSString *)engine engineDisplacementLiters:(SharedDouble * _Nullable)engineDisplacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount tankCapacity:(double)tankCapacity factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg learnedMpg:(double)learnedMpg preferredFuelType:(NSString *)preferredFuelType preferredFuelGrade:(NSString * _Nullable)preferredFuelGrade unitSystem:(NSString *)unitSystem reserveFuelGallons:(double)reserveFuelGallons lowFuelThresholdPercent:(double)lowFuelThresholdPercent __attribute__((swift_name("insertVehicle(id:year:make:model:trim:color:engine:engineDisplacementLiters:cylinderCount:tankCapacity:factoryCityMpg:factoryHwyMpg:learnedMpg:preferredFuelType:preferredFuelGrade:unitSystem:reserveFuelGallons:lowFuelThresholdPercent:)")));
- (void)updateLearnedMpgLearnedMpg:(double)learnedMpg id:(NSString *)id __attribute__((swift_name("updateLearnedMpg(learnedMpg:id:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Trip_")))
@interface SharedTrip_ : SharedBase
- (instancetype)initWithId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp distance:(double)distance duration:(int64_t)duration idleTime:(int64_t)idleTime averageSpeed:(double)averageSpeed drivingType:(NSString *)drivingType fuelBurned:(double)fuelBurned __attribute__((swift_name("init(id:vehicleId:timestamp:distance:duration:idleTime:averageSpeed:drivingType:fuelBurned:)"))) __attribute__((objc_designated_initializer));
- (SharedTrip_ *)doCopyId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp distance:(double)distance duration:(int64_t)duration idleTime:(int64_t)idleTime averageSpeed:(double)averageSpeed drivingType:(NSString *)drivingType fuelBurned:(double)fuelBurned __attribute__((swift_name("doCopy(id:vehicleId:timestamp:distance:duration:idleTime:averageSpeed:drivingType:fuelBurned:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) double averageSpeed __attribute__((swift_name("averageSpeed")));
@property (readonly) double distance __attribute__((swift_name("distance")));
@property (readonly) NSString *drivingType __attribute__((swift_name("drivingType")));
@property (readonly) int64_t duration __attribute__((swift_name("duration")));
@property (readonly) double fuelBurned __attribute__((swift_name("fuelBurned")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) int64_t idleTime __attribute__((swift_name("idleTime")));
@property (readonly) int64_t timestamp __attribute__((swift_name("timestamp")));
@property (readonly) NSString *vehicleId __attribute__((swift_name("vehicleId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Vehicle")))
@interface SharedVehicle : SharedBase
- (instancetype)initWithId:(NSString *)id year:(int64_t)year make:(NSString *)make model:(NSString *)model trim:(NSString * _Nullable)trim color:(NSString * _Nullable)color engine:(NSString *)engine engineDisplacementLiters:(SharedDouble * _Nullable)engineDisplacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount tankCapacity:(double)tankCapacity factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg learnedMpg:(double)learnedMpg preferredFuelType:(NSString *)preferredFuelType preferredFuelGrade:(NSString * _Nullable)preferredFuelGrade unitSystem:(NSString *)unitSystem reserveFuelGallons:(double)reserveFuelGallons lowFuelThresholdPercent:(double)lowFuelThresholdPercent __attribute__((swift_name("init(id:year:make:model:trim:color:engine:engineDisplacementLiters:cylinderCount:tankCapacity:factoryCityMpg:factoryHwyMpg:learnedMpg:preferredFuelType:preferredFuelGrade:unitSystem:reserveFuelGallons:lowFuelThresholdPercent:)"))) __attribute__((objc_designated_initializer));
- (SharedVehicle *)doCopyId:(NSString *)id year:(int64_t)year make:(NSString *)make model:(NSString *)model trim:(NSString * _Nullable)trim color:(NSString * _Nullable)color engine:(NSString *)engine engineDisplacementLiters:(SharedDouble * _Nullable)engineDisplacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount tankCapacity:(double)tankCapacity factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg learnedMpg:(double)learnedMpg preferredFuelType:(NSString *)preferredFuelType preferredFuelGrade:(NSString * _Nullable)preferredFuelGrade unitSystem:(NSString *)unitSystem reserveFuelGallons:(double)reserveFuelGallons lowFuelThresholdPercent:(double)lowFuelThresholdPercent __attribute__((swift_name("doCopy(id:year:make:model:trim:color:engine:engineDisplacementLiters:cylinderCount:tankCapacity:factoryCityMpg:factoryHwyMpg:learnedMpg:preferredFuelType:preferredFuelGrade:unitSystem:reserveFuelGallons:lowFuelThresholdPercent:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable color __attribute__((swift_name("color")));
@property (readonly) SharedLong * _Nullable cylinderCount __attribute__((swift_name("cylinderCount")));
@property (readonly) NSString *engine __attribute__((swift_name("engine")));
@property (readonly) SharedDouble * _Nullable engineDisplacementLiters __attribute__((swift_name("engineDisplacementLiters")));
@property (readonly) double factoryCityMpg __attribute__((swift_name("factoryCityMpg")));
@property (readonly) double factoryHwyMpg __attribute__((swift_name("factoryHwyMpg")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) double learnedMpg __attribute__((swift_name("learnedMpg")));
@property (readonly) double lowFuelThresholdPercent __attribute__((swift_name("lowFuelThresholdPercent")));
@property (readonly) NSString *make __attribute__((swift_name("make")));
@property (readonly) NSString *model __attribute__((swift_name("model")));
@property (readonly) NSString * _Nullable preferredFuelGrade __attribute__((swift_name("preferredFuelGrade")));
@property (readonly) NSString *preferredFuelType __attribute__((swift_name("preferredFuelType")));
@property (readonly) double reserveFuelGallons __attribute__((swift_name("reserveFuelGallons")));
@property (readonly) double tankCapacity __attribute__((swift_name("tankCapacity")));
@property (readonly) NSString * _Nullable trim __attribute__((swift_name("trim")));
@property (readonly) NSString *unitSystem __attribute__((swift_name("unitSystem")));
@property (readonly) int64_t year __attribute__((swift_name("year")));
@end

__attribute__((swift_name("FillUpRepository")))
@protocol SharedFillUpRepository
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteFillUpId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteFillUp(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getFillUpsVehicleId:(NSString *)vehicleId __attribute__((swift_name("getFillUps(vehicleId:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getRecentFillUpsVehicleId:(NSString *)vehicleId limit:(int64_t)limit __attribute__((swift_name("getRecentFillUps(vehicleId:limit:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveFillUpFillUp:(SharedFillUp *)fillUp completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveFillUp(fillUp:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SqlDelightFillUpRepository")))
@interface SharedSqlDelightFillUpRepository : SharedBase <SharedFillUpRepository>
- (instancetype)initWithDb:(id<SharedTankPilotDb>)db dispatcher:(SharedKotlinx_coroutines_coreCoroutineDispatcher *)dispatcher __attribute__((swift_name("init(db:dispatcher:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteFillUpId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteFillUp(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getFillUpsVehicleId:(NSString *)vehicleId __attribute__((swift_name("getFillUps(vehicleId:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getRecentFillUpsVehicleId:(NSString *)vehicleId limit:(int64_t)limit __attribute__((swift_name("getRecentFillUps(vehicleId:limit:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveFillUpFillUp:(SharedFillUp *)fillUp completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveFillUp(fillUp:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FillUp")))
@interface SharedFillUp : SharedBase
- (instancetype)initWithId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp gallonsAdded:(double)gallonsAdded price:(double)price odometer:(SharedDouble * _Nullable)odometer isFull:(BOOL)isFull __attribute__((swift_name("init(id:vehicleId:timestamp:gallonsAdded:price:odometer:isFull:)"))) __attribute__((objc_designated_initializer));
- (SharedFillUp *)doCopyId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp gallonsAdded:(double)gallonsAdded price:(double)price odometer:(SharedDouble * _Nullable)odometer isFull:(BOOL)isFull __attribute__((swift_name("doCopy(id:vehicleId:timestamp:gallonsAdded:price:odometer:isFull:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) double gallonsAdded __attribute__((swift_name("gallonsAdded")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) BOOL isFull __attribute__((swift_name("isFull")));
@property (readonly) SharedDouble * _Nullable odometer __attribute__((swift_name("odometer")));
@property (readonly) double price __attribute__((swift_name("price")));
@property (readonly) int64_t timestamp __attribute__((swift_name("timestamp")));
@property (readonly) NSString *vehicleId __attribute__((swift_name("vehicleId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelEngine")))
@interface SharedFuelEngine : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)fuelEngine __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedFuelEngine *shared __attribute__((swift_name("shared")));
- (double)calculateSafeRangeRemainingFuel:(double)remainingFuel learnedMpg:(double)learnedMpg confidenceLevel:(SharedConfidenceLevel *)confidenceLevel __attribute__((swift_name("calculateSafeRange(remainingFuel:learnedMpg:confidenceLevel:)")));
- (double)estimateFuelBurnedDistance:(double)distance durationSeconds:(int64_t)durationSeconds idleTimeSeconds:(int64_t)idleTimeSeconds drivingType:(SharedDrivingType *)drivingType displacementLiters:(SharedDouble * _Nullable)displacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount learnedMpg:(double)learnedMpg factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg __attribute__((swift_name("estimateFuelBurned(distance:durationSeconds:idleTimeSeconds:drivingType:displacementLiters:cylinderCount:learnedMpg:factoryCityMpg:factoryHwyMpg:)")));
- (double)estimateIdleFuelRateDisplacementLiters:(SharedDouble * _Nullable)displacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount __attribute__((swift_name("estimateIdleFuelRate(displacementLiters:cylinderCount:)")));
- (double)getConfidenceSafetyFactorConfidenceLevel:(SharedConfidenceLevel *)confidenceLevel __attribute__((swift_name("getConfidenceSafetyFactor(confidenceLevel:)")));
- (double)recalibrateMpgCurrentLearnedMpg:(double)currentLearnedMpg distanceTraveled:(double)distanceTraveled totalGallonsAdded:(double)totalGallonsAdded learningRate:(double)learningRate __attribute__((swift_name("recalibrateMpg(currentLearnedMpg:distanceTraveled:totalGallonsAdded:learningRate:)")));
@end

__attribute__((swift_name("FuelStationProvider")))
@protocol SharedFuelStationProvider
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getNearbyStationsLatitude:(double)latitude longitude:(double)longitude radiusMiles:(double)radiusMiles fuelType:(SharedFuelType *)fuelType completionHandler:(void (^)(NSArray<SharedFuelStation *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("getNearbyStations(latitude:longitude:radiusMiles:fuelType:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MockFuelStationProvider")))
@interface SharedMockFuelStationProvider : SharedBase <SharedFuelStationProvider>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getNearbyStationsLatitude:(double)latitude longitude:(double)longitude radiusMiles:(double)radiusMiles fuelType:(SharedFuelType *)fuelType completionHandler:(void (^)(NSArray<SharedFuelStation *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("getNearbyStations(latitude:longitude:radiusMiles:fuelType:completionHandler:)")));
@end

__attribute__((swift_name("FuelStationRepository")))
@protocol SharedFuelStationRepository
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)clearCacheWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("clearCache(completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getCachedStations __attribute__((swift_name("getCachedStations()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)refreshStationsLatitude:(double)latitude longitude:(double)longitude radiusMiles:(double)radiusMiles fuelType:(SharedFuelType *)fuelType forceRefresh:(BOOL)forceRefresh completionHandler:(void (^)(NSArray<SharedFuelStation *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("refreshStations(latitude:longitude:radiusMiles:fuelType:forceRefresh:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SqlDelightFuelStationRepository")))
@interface SharedSqlDelightFuelStationRepository : SharedBase <SharedFuelStationRepository>
- (instancetype)initWithDb:(id<SharedTankPilotDb>)db provider:(id<SharedFuelStationProvider>)provider dispatcher:(SharedKotlinx_coroutines_coreCoroutineDispatcher *)dispatcher cacheDurationMs:(int64_t)cacheDurationMs __attribute__((swift_name("init(db:provider:dispatcher:cacheDurationMs:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)clearCacheWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("clearCache(completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getCachedStations __attribute__((swift_name("getCachedStations()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)refreshStationsLatitude:(double)latitude longitude:(double)longitude radiusMiles:(double)radiusMiles fuelType:(SharedFuelType *)fuelType forceRefresh:(BOOL)forceRefresh completionHandler:(void (^)(NSArray<SharedFuelStation *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("refreshStations(latitude:longitude:radiusMiles:fuelType:forceRefresh:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelRescueEngine")))
@interface SharedFuelRescueEngine : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)fuelRescueEngine __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedFuelRescueEngine *shared __attribute__((swift_name("shared")));
- (NSArray<SharedFuelStationRecommendation *> *)evaluateStationsEstimatedRemaining:(double)estimatedRemaining learnedMpg:(double)learnedMpg confidenceLevel:(SharedConfidenceLevel *)confidenceLevel vehicleFuelType:(SharedFuelType *)vehicleFuelType vehicleFuelGradeKey:(NSString *)vehicleFuelGradeKey reserveFuel:(double)reserveFuel tankCapacity:(double)tankCapacity candidates:(NSArray<SharedFuelStation *> *)candidates routeDistances:(NSDictionary<SharedStationId *, SharedKotlinPair<SharedDouble *, SharedDouble *> *> *)routeDistances fallbackPrice:(SharedFuelPricePerUnit * _Nullable)fallbackPrice __attribute__((swift_name("evaluateStations(estimatedRemaining:learnedMpg:confidenceLevel:vehicleFuelType:vehicleFuelGradeKey:reserveFuel:tankCapacity:candidates:routeDistances:fallbackPrice:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelStation")))
@interface SharedFuelStation : SharedBase
- (instancetype)initWithId:(SharedStationId *)id name:(NSString *)name brand:(NSString * _Nullable)brand latitude:(double)latitude longitude:(double)longitude address:(NSString * _Nullable)address distanceMiles:(double)distanceMiles routeDistanceMiles:(SharedDouble * _Nullable)routeDistanceMiles estimatedDriveMinutes:(SharedDouble * _Nullable)estimatedDriveMinutes isOpen:(SharedBoolean * _Nullable)isOpen navigationDestination:(NSString * _Nullable)navigationDestination fuelPrices:(NSArray<SharedStationFuelPrice *> *)fuelPrices lastFetchedAt:(int64_t)lastFetchedAt __attribute__((swift_name("init(id:name:brand:latitude:longitude:address:distanceMiles:routeDistanceMiles:estimatedDriveMinutes:isOpen:navigationDestination:fuelPrices:lastFetchedAt:)"))) __attribute__((objc_designated_initializer));
- (SharedFuelStation *)doCopyId:(SharedStationId *)id name:(NSString *)name brand:(NSString * _Nullable)brand latitude:(double)latitude longitude:(double)longitude address:(NSString * _Nullable)address distanceMiles:(double)distanceMiles routeDistanceMiles:(SharedDouble * _Nullable)routeDistanceMiles estimatedDriveMinutes:(SharedDouble * _Nullable)estimatedDriveMinutes isOpen:(SharedBoolean * _Nullable)isOpen navigationDestination:(NSString * _Nullable)navigationDestination fuelPrices:(NSArray<SharedStationFuelPrice *> *)fuelPrices lastFetchedAt:(int64_t)lastFetchedAt __attribute__((swift_name("doCopy(id:name:brand:latitude:longitude:address:distanceMiles:routeDistanceMiles:estimatedDriveMinutes:isOpen:navigationDestination:fuelPrices:lastFetchedAt:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable address __attribute__((swift_name("address")));
@property (readonly) NSString * _Nullable brand __attribute__((swift_name("brand")));
@property (readonly) double distanceMiles __attribute__((swift_name("distanceMiles")));
@property (readonly) SharedDouble * _Nullable estimatedDriveMinutes __attribute__((swift_name("estimatedDriveMinutes")));
@property (readonly) NSArray<SharedStationFuelPrice *> *fuelPrices __attribute__((swift_name("fuelPrices")));
@property (readonly) SharedStationId *id __attribute__((swift_name("id")));
@property (readonly) SharedBoolean * _Nullable isOpen __attribute__((swift_name("isOpen")));
@property (readonly) int64_t lastFetchedAt __attribute__((swift_name("lastFetchedAt")));
@property (readonly) double latitude __attribute__((swift_name("latitude")));
@property (readonly) double longitude __attribute__((swift_name("longitude")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) NSString * _Nullable navigationDestination __attribute__((swift_name("navigationDestination")));
@property (readonly) SharedDouble * _Nullable routeDistanceMiles __attribute__((swift_name("routeDistanceMiles")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FuelStationRecommendation")))
@interface SharedFuelStationRecommendation : SharedBase
- (instancetype)initWithStation:(SharedFuelStation *)station reachabilityStatus:(SharedReachabilityStatus *)reachabilityStatus estimatedFuelUsedToReach:(double)estimatedFuelUsedToReach estimatedFuelRemainingOnArrival:(double)estimatedFuelRemainingOnArrival advertisedPrice:(SharedFuelPricePerUnit * _Nullable)advertisedPrice effectiveTripCost:(SharedMoney *)effectiveTripCost estimatedFillCost:(SharedMoney *)estimatedFillCost estimatedSavings:(SharedMoney *)estimatedSavings detourMiles:(double)detourMiles priceFreshness:(SharedPriceFreshness *)priceFreshness recommendationScore:(double)recommendationScore recommendationReasons:(NSArray<NSString *> *)recommendationReasons warningMessages:(NSArray<NSString *> *)warningMessages __attribute__((swift_name("init(station:reachabilityStatus:estimatedFuelUsedToReach:estimatedFuelRemainingOnArrival:advertisedPrice:effectiveTripCost:estimatedFillCost:estimatedSavings:detourMiles:priceFreshness:recommendationScore:recommendationReasons:warningMessages:)"))) __attribute__((objc_designated_initializer));
- (SharedFuelStationRecommendation *)doCopyStation:(SharedFuelStation *)station reachabilityStatus:(SharedReachabilityStatus *)reachabilityStatus estimatedFuelUsedToReach:(double)estimatedFuelUsedToReach estimatedFuelRemainingOnArrival:(double)estimatedFuelRemainingOnArrival advertisedPrice:(SharedFuelPricePerUnit * _Nullable)advertisedPrice effectiveTripCost:(SharedMoney *)effectiveTripCost estimatedFillCost:(SharedMoney *)estimatedFillCost estimatedSavings:(SharedMoney *)estimatedSavings detourMiles:(double)detourMiles priceFreshness:(SharedPriceFreshness *)priceFreshness recommendationScore:(double)recommendationScore recommendationReasons:(NSArray<NSString *> *)recommendationReasons warningMessages:(NSArray<NSString *> *)warningMessages __attribute__((swift_name("doCopy(station:reachabilityStatus:estimatedFuelUsedToReach:estimatedFuelRemainingOnArrival:advertisedPrice:effectiveTripCost:estimatedFillCost:estimatedSavings:detourMiles:priceFreshness:recommendationScore:recommendationReasons:warningMessages:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedFuelPricePerUnit * _Nullable advertisedPrice __attribute__((swift_name("advertisedPrice")));
@property (readonly) double detourMiles __attribute__((swift_name("detourMiles")));
@property (readonly) SharedMoney *effectiveTripCost __attribute__((swift_name("effectiveTripCost")));
@property (readonly) SharedMoney *estimatedFillCost __attribute__((swift_name("estimatedFillCost")));
@property (readonly) double estimatedFuelRemainingOnArrival __attribute__((swift_name("estimatedFuelRemainingOnArrival")));
@property (readonly) double estimatedFuelUsedToReach __attribute__((swift_name("estimatedFuelUsedToReach")));
@property (readonly) SharedMoney *estimatedSavings __attribute__((swift_name("estimatedSavings")));
@property (readonly) SharedPriceFreshness *priceFreshness __attribute__((swift_name("priceFreshness")));
@property (readonly) SharedReachabilityStatus *reachabilityStatus __attribute__((swift_name("reachabilityStatus")));
@property (readonly) NSArray<NSString *> *recommendationReasons __attribute__((swift_name("recommendationReasons")));
@property (readonly) double recommendationScore __attribute__((swift_name("recommendationScore")));
@property (readonly) SharedFuelStation *station __attribute__((swift_name("station")));
@property (readonly) NSArray<NSString *> *warningMessages __attribute__((swift_name("warningMessages")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PriceFreshness")))
@interface SharedPriceFreshness : SharedKotlinEnum<SharedPriceFreshness *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedPriceFreshness *recent __attribute__((swift_name("recent")));
@property (class, readonly) SharedPriceFreshness *aging __attribute__((swift_name("aging")));
@property (class, readonly) SharedPriceFreshness *stale __attribute__((swift_name("stale")));
@property (class, readonly) SharedPriceFreshness *unknown __attribute__((swift_name("unknown")));
+ (SharedKotlinArray<SharedPriceFreshness *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedPriceFreshness *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ReachabilityStatus")))
@interface SharedReachabilityStatus : SharedKotlinEnum<SharedReachabilityStatus *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedReachabilityStatus *safelyReachable __attribute__((swift_name("safelyReachable")));
@property (class, readonly) SharedReachabilityStatus *marginallyReachable __attribute__((swift_name("marginallyReachable")));
@property (class, readonly) SharedReachabilityStatus *outsideSafeRange __attribute__((swift_name("outsideSafeRange")));
@property (class, readonly) SharedReachabilityStatus *unknown __attribute__((swift_name("unknown")));
+ (SharedKotlinArray<SharedReachabilityStatus *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedReachabilityStatus *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("RouteDistanceProvider")))
@protocol SharedRouteDistanceProvider
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)calculateRouteDistancesOriginLat:(double)originLat originLng:(double)originLng destinations:(NSArray<SharedKotlinPair<NSString *, SharedKotlinPair<SharedDouble *, SharedDouble *> *> *> *)destinations completionHandler:(void (^)(NSDictionary<NSString *, SharedKotlinPair<SharedDouble *, SharedDouble *> *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("calculateRouteDistances(originLat:originLng:destinations:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StationFuelPrice")))
@interface SharedStationFuelPrice : SharedBase
- (instancetype)initWithFuelType:(SharedFuelType *)fuelType fuelGradeKey:(NSString *)fuelGradeKey displayFuelGrade:(NSString * _Nullable)displayFuelGrade price:(SharedFuelPricePerUnit *)price updatedAt:(SharedLong * _Nullable)updatedAt freshness:(SharedPriceFreshness *)freshness source:(NSString *)source __attribute__((swift_name("init(fuelType:fuelGradeKey:displayFuelGrade:price:updatedAt:freshness:source:)"))) __attribute__((objc_designated_initializer));
- (SharedStationFuelPrice *)doCopyFuelType:(SharedFuelType *)fuelType fuelGradeKey:(NSString *)fuelGradeKey displayFuelGrade:(NSString * _Nullable)displayFuelGrade price:(SharedFuelPricePerUnit *)price updatedAt:(SharedLong * _Nullable)updatedAt freshness:(SharedPriceFreshness *)freshness source:(NSString *)source __attribute__((swift_name("doCopy(fuelType:fuelGradeKey:displayFuelGrade:price:updatedAt:freshness:source:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable displayFuelGrade __attribute__((swift_name("displayFuelGrade")));
@property (readonly) SharedPriceFreshness *freshness __attribute__((swift_name("freshness")));
@property (readonly) NSString *fuelGradeKey __attribute__((swift_name("fuelGradeKey")));
@property (readonly) SharedFuelType *fuelType __attribute__((swift_name("fuelType")));
@property (readonly) SharedFuelPricePerUnit *price __attribute__((swift_name("price")));
@property (readonly) NSString *source __attribute__((swift_name("source")));
@property (readonly) SharedLong * _Nullable updatedAt __attribute__((swift_name("updatedAt")));
@end

__attribute__((swift_name("VehicleTelemetryProvider")))
@protocol SharedVehicleTelemetryProvider
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)connectWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("connect(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)disconnectWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("disconnect(completionHandler:)")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> capabilitiesFlow __attribute__((swift_name("capabilitiesFlow")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> metadataFlow __attribute__((swift_name("metadataFlow")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> telemetryFlow __attribute__((swift_name("telemetryFlow")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MockTelemetryProvider")))
@interface SharedMockTelemetryProvider : SharedBase <SharedVehicleTelemetryProvider>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)connectWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("connect(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)disconnectWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("disconnect(completionHandler:)")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> capabilitiesFlow __attribute__((swift_name("capabilitiesFlow")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> metadataFlow __attribute__((swift_name("metadataFlow")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> telemetryFlow __attribute__((swift_name("telemetryFlow")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ObdParser")))
@interface SharedObdParser : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)obdParser __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedObdParser *shared __attribute__((swift_name("shared")));
- (NSString *)cleanResponseRaw:(NSString *)raw __attribute__((swift_name("cleanResponse(raw:)")));
- (SharedDouble * _Nullable)parseCoolantTempRaw:(NSString *)raw __attribute__((swift_name("parseCoolantTemp(raw:)")));
- (SharedDouble * _Nullable)parseEngineLoadRaw:(NSString *)raw __attribute__((swift_name("parseEngineLoad(raw:)")));
- (SharedDouble * _Nullable)parseMafRaw:(NSString *)raw __attribute__((swift_name("parseMaf(raw:)")));
- (SharedDouble * _Nullable)parseRpmRaw:(NSString *)raw __attribute__((swift_name("parseRpm(raw:)")));
- (SharedDouble * _Nullable)parseSpeedRaw:(NSString *)raw __attribute__((swift_name("parseSpeed(raw:)")));
- (NSString * _Nullable)parseVinRaw:(NSString *)raw __attribute__((swift_name("parseVin(raw:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ConnectionStatus")))
@interface SharedConnectionStatus : SharedKotlinEnum<SharedConnectionStatus *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedConnectionStatus *disconnected __attribute__((swift_name("disconnected")));
@property (class, readonly) SharedConnectionStatus *connecting __attribute__((swift_name("connecting")));
@property (class, readonly) SharedConnectionStatus *connected __attribute__((swift_name("connected")));
@property (class, readonly) SharedConnectionStatus *reconnecting __attribute__((swift_name("reconnecting")));
+ (SharedKotlinArray<SharedConnectionStatus *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedConnectionStatus *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ObdCapabilities")))
@interface SharedObdCapabilities : SharedBase
- (instancetype)initWithSupportedMode01Pids:(NSSet<SharedInt *> *)supportedMode01Pids supportsVin:(BOOL)supportsVin supportsStoredDtcs:(BOOL)supportsStoredDtcs supportsPendingDtcs:(BOOL)supportsPendingDtcs supportsPermanentDtcs:(BOOL)supportsPermanentDtcs detectedProtocol:(NSString * _Nullable)detectedProtocol adapterVersion:(NSString * _Nullable)adapterVersion __attribute__((swift_name("init(supportedMode01Pids:supportsVin:supportsStoredDtcs:supportsPendingDtcs:supportsPermanentDtcs:detectedProtocol:adapterVersion:)"))) __attribute__((objc_designated_initializer));
- (SharedObdCapabilities *)doCopySupportedMode01Pids:(NSSet<SharedInt *> *)supportedMode01Pids supportsVin:(BOOL)supportsVin supportsStoredDtcs:(BOOL)supportsStoredDtcs supportsPendingDtcs:(BOOL)supportsPendingDtcs supportsPermanentDtcs:(BOOL)supportsPermanentDtcs detectedProtocol:(NSString * _Nullable)detectedProtocol adapterVersion:(NSString * _Nullable)adapterVersion __attribute__((swift_name("doCopy(supportedMode01Pids:supportsVin:supportsStoredDtcs:supportsPendingDtcs:supportsPermanentDtcs:detectedProtocol:adapterVersion:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable adapterVersion __attribute__((swift_name("adapterVersion")));
@property (readonly) NSString * _Nullable detectedProtocol __attribute__((swift_name("detectedProtocol")));
@property (readonly) NSSet<SharedInt *> *supportedMode01Pids __attribute__((swift_name("supportedMode01Pids")));
@property (readonly) BOOL supportsPendingDtcs __attribute__((swift_name("supportsPendingDtcs")));
@property (readonly) BOOL supportsPermanentDtcs __attribute__((swift_name("supportsPermanentDtcs")));
@property (readonly) BOOL supportsStoredDtcs __attribute__((swift_name("supportsStoredDtcs")));
@property (readonly) BOOL supportsVin __attribute__((swift_name("supportsVin")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TelemetryData")))
@interface SharedTelemetryData : SharedBase
- (instancetype)initWithSpeedKmh:(SharedDouble * _Nullable)speedKmh engineRpm:(SharedDouble * _Nullable)engineRpm engineLoadPercent:(SharedDouble * _Nullable)engineLoadPercent coolantTempCelsius:(SharedDouble * _Nullable)coolantTempCelsius intakeAirTempCelsius:(SharedDouble * _Nullable)intakeAirTempCelsius batteryVoltage:(SharedDouble * _Nullable)batteryVoltage vin:(NSString * _Nullable)vin engineRuntimeSeconds:(SharedLong * _Nullable)engineRuntimeSeconds checkEngineLightOn:(SharedBoolean * _Nullable)checkEngineLightOn diagnosticTroubleCodes:(NSArray<NSString *> *)diagnosticTroubleCodes fuelTrimPercent:(SharedDouble * _Nullable)fuelTrimPercent massAirFlowGps:(SharedDouble * _Nullable)massAirFlowGps __attribute__((swift_name("init(speedKmh:engineRpm:engineLoadPercent:coolantTempCelsius:intakeAirTempCelsius:batteryVoltage:vin:engineRuntimeSeconds:checkEngineLightOn:diagnosticTroubleCodes:fuelTrimPercent:massAirFlowGps:)"))) __attribute__((objc_designated_initializer));
- (SharedTelemetryData *)doCopySpeedKmh:(SharedDouble * _Nullable)speedKmh engineRpm:(SharedDouble * _Nullable)engineRpm engineLoadPercent:(SharedDouble * _Nullable)engineLoadPercent coolantTempCelsius:(SharedDouble * _Nullable)coolantTempCelsius intakeAirTempCelsius:(SharedDouble * _Nullable)intakeAirTempCelsius batteryVoltage:(SharedDouble * _Nullable)batteryVoltage vin:(NSString * _Nullable)vin engineRuntimeSeconds:(SharedLong * _Nullable)engineRuntimeSeconds checkEngineLightOn:(SharedBoolean * _Nullable)checkEngineLightOn diagnosticTroubleCodes:(NSArray<NSString *> *)diagnosticTroubleCodes fuelTrimPercent:(SharedDouble * _Nullable)fuelTrimPercent massAirFlowGps:(SharedDouble * _Nullable)massAirFlowGps __attribute__((swift_name("doCopy(speedKmh:engineRpm:engineLoadPercent:coolantTempCelsius:intakeAirTempCelsius:batteryVoltage:vin:engineRuntimeSeconds:checkEngineLightOn:diagnosticTroubleCodes:fuelTrimPercent:massAirFlowGps:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedDouble * _Nullable batteryVoltage __attribute__((swift_name("batteryVoltage")));
@property (readonly) SharedBoolean * _Nullable checkEngineLightOn __attribute__((swift_name("checkEngineLightOn")));
@property (readonly) SharedDouble * _Nullable coolantTempCelsius __attribute__((swift_name("coolantTempCelsius")));
@property (readonly) NSArray<NSString *> *diagnosticTroubleCodes __attribute__((swift_name("diagnosticTroubleCodes")));
@property (readonly) SharedDouble * _Nullable engineLoadPercent __attribute__((swift_name("engineLoadPercent")));
@property (readonly) SharedDouble * _Nullable engineRpm __attribute__((swift_name("engineRpm")));
@property (readonly) SharedLong * _Nullable engineRuntimeSeconds __attribute__((swift_name("engineRuntimeSeconds")));
@property (readonly) SharedDouble * _Nullable fuelTrimPercent __attribute__((swift_name("fuelTrimPercent")));
@property (readonly) SharedDouble * _Nullable intakeAirTempCelsius __attribute__((swift_name("intakeAirTempCelsius")));
@property (readonly) SharedDouble * _Nullable massAirFlowGps __attribute__((swift_name("massAirFlowGps")));
@property (readonly) SharedDouble * _Nullable speedKmh __attribute__((swift_name("speedKmh")));
@property (readonly) NSString * _Nullable vin __attribute__((swift_name("vin")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TelemetryMetadata")))
@interface SharedTelemetryMetadata : SharedBase
- (instancetype)initWithAdapterName:(NSString * _Nullable)adapterName signalStrengthDbm:(SharedInt * _Nullable)signalStrengthDbm connectionStatus:(SharedConnectionStatus *)connectionStatus __attribute__((swift_name("init(adapterName:signalStrengthDbm:connectionStatus:)"))) __attribute__((objc_designated_initializer));
- (SharedTelemetryMetadata *)doCopyAdapterName:(NSString * _Nullable)adapterName signalStrengthDbm:(SharedInt * _Nullable)signalStrengthDbm connectionStatus:(SharedConnectionStatus *)connectionStatus __attribute__((swift_name("doCopy(adapterName:signalStrengthDbm:connectionStatus:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable adapterName __attribute__((swift_name("adapterName")));
@property (readonly) SharedConnectionStatus *connectionStatus __attribute__((swift_name("connectionStatus")));
@property (readonly) SharedInt * _Nullable signalStrengthDbm __attribute__((swift_name("signalStrengthDbm")));
@end

__attribute__((swift_name("TripRepository")))
@protocol SharedTripRepository
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteTripId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteTrip(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getRecentTripsVehicleId:(NSString *)vehicleId limit:(int64_t)limit __attribute__((swift_name("getRecentTrips(vehicleId:limit:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getTripsVehicleId:(NSString *)vehicleId __attribute__((swift_name("getTrips(vehicleId:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveTripTrip:(SharedTrip *)trip completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveTrip(trip:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SqlDelightTripRepository")))
@interface SharedSqlDelightTripRepository : SharedBase <SharedTripRepository>
- (instancetype)initWithDb:(id<SharedTankPilotDb>)db dispatcher:(SharedKotlinx_coroutines_coreCoroutineDispatcher *)dispatcher __attribute__((swift_name("init(db:dispatcher:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteTripId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteTrip(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getRecentTripsVehicleId:(NSString *)vehicleId limit:(int64_t)limit __attribute__((swift_name("getRecentTrips(vehicleId:limit:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getTripsVehicleId:(NSString *)vehicleId __attribute__((swift_name("getTrips(vehicleId:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveTripTrip:(SharedTrip *)trip completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveTrip(trip:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DrivingType")))
@interface SharedDrivingType : SharedKotlinEnum<SharedDrivingType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedDrivingType *city __attribute__((swift_name("city")));
@property (class, readonly) SharedDrivingType *highway __attribute__((swift_name("highway")));
@property (class, readonly) SharedDrivingType *mixed __attribute__((swift_name("mixed")));
+ (SharedKotlinArray<SharedDrivingType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedDrivingType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Trip")))
@interface SharedTrip : SharedBase
- (instancetype)initWithId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp distance:(double)distance duration:(int64_t)duration idleTime:(int64_t)idleTime averageSpeed:(double)averageSpeed drivingType:(SharedDrivingType *)drivingType fuelBurned:(double)fuelBurned __attribute__((swift_name("init(id:vehicleId:timestamp:distance:duration:idleTime:averageSpeed:drivingType:fuelBurned:)"))) __attribute__((objc_designated_initializer));
- (SharedTrip *)doCopyId:(NSString *)id vehicleId:(NSString *)vehicleId timestamp:(int64_t)timestamp distance:(double)distance duration:(int64_t)duration idleTime:(int64_t)idleTime averageSpeed:(double)averageSpeed drivingType:(SharedDrivingType *)drivingType fuelBurned:(double)fuelBurned __attribute__((swift_name("doCopy(id:vehicleId:timestamp:distance:duration:idleTime:averageSpeed:drivingType:fuelBurned:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) double averageSpeed __attribute__((swift_name("averageSpeed")));
@property (readonly) double distance __attribute__((swift_name("distance")));
@property (readonly) SharedDrivingType *drivingType __attribute__((swift_name("drivingType")));
@property (readonly) int64_t duration __attribute__((swift_name("duration")));
@property (readonly) double fuelBurned __attribute__((swift_name("fuelBurned")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) int64_t idleTime __attribute__((swift_name("idleTime")));
@property (readonly) int64_t timestamp __attribute__((swift_name("timestamp")));
@property (readonly) NSString *vehicleId __attribute__((swift_name("vehicleId")));
@end

__attribute__((swift_name("VehicleRepository")))
@protocol SharedVehicleRepository
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteVehicleId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteVehicle(id:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getVehicleByIdId:(NSString *)id completionHandler:(void (^)(SharedVehicle_ * _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getVehicleById(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getVehicles __attribute__((swift_name("getVehicles()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveVehicleVehicle:(SharedVehicle_ *)vehicle completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveVehicle(vehicle:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)updateLearnedMpgVehicleId:(NSString *)vehicleId learnedMpg:(double)learnedMpg completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("updateLearnedMpg(vehicleId:learnedMpg:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SqlDelightVehicleRepository")))
@interface SharedSqlDelightVehicleRepository : SharedBase <SharedVehicleRepository>
- (instancetype)initWithDb:(id<SharedTankPilotDb>)db dispatcher:(SharedKotlinx_coroutines_coreCoroutineDispatcher *)dispatcher __attribute__((swift_name("init(db:dispatcher:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)deleteVehicleId:(NSString *)id completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("deleteVehicle(id:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getVehicleByIdId:(NSString *)id completionHandler:(void (^)(SharedVehicle_ * _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getVehicleById(id:completionHandler:)")));
- (id<SharedKotlinx_coroutines_coreFlow>)getVehicles __attribute__((swift_name("getVehicles()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)saveVehicleVehicle:(SharedVehicle_ *)vehicle completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("saveVehicle(vehicle:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)updateLearnedMpgVehicleId:(NSString *)vehicleId learnedMpg:(double)learnedMpg completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("updateLearnedMpg(vehicleId:learnedMpg:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Vehicle_")))
@interface SharedVehicle_ : SharedBase
- (instancetype)initWithId:(NSString *)id year:(int32_t)year make:(NSString *)make model:(NSString *)model trim:(NSString * _Nullable)trim color:(NSString * _Nullable)color engine:(NSString *)engine engineDisplacementLiters:(SharedDouble * _Nullable)engineDisplacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount tankCapacity:(double)tankCapacity factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg learnedMpg:(double)learnedMpg preferredFuelType:(SharedFuelType *)preferredFuelType preferredFuelGrade:(NSString * _Nullable)preferredFuelGrade unitSystem:(SharedUnitSystem *)unitSystem reserveFuelGallons:(double)reserveFuelGallons lowFuelThresholdPercent:(double)lowFuelThresholdPercent __attribute__((swift_name("init(id:year:make:model:trim:color:engine:engineDisplacementLiters:cylinderCount:tankCapacity:factoryCityMpg:factoryHwyMpg:learnedMpg:preferredFuelType:preferredFuelGrade:unitSystem:reserveFuelGallons:lowFuelThresholdPercent:)"))) __attribute__((objc_designated_initializer));
- (SharedVehicle_ *)doCopyId:(NSString *)id year:(int32_t)year make:(NSString *)make model:(NSString *)model trim:(NSString * _Nullable)trim color:(NSString * _Nullable)color engine:(NSString *)engine engineDisplacementLiters:(SharedDouble * _Nullable)engineDisplacementLiters cylinderCount:(SharedLong * _Nullable)cylinderCount tankCapacity:(double)tankCapacity factoryCityMpg:(double)factoryCityMpg factoryHwyMpg:(double)factoryHwyMpg learnedMpg:(double)learnedMpg preferredFuelType:(SharedFuelType *)preferredFuelType preferredFuelGrade:(NSString * _Nullable)preferredFuelGrade unitSystem:(SharedUnitSystem *)unitSystem reserveFuelGallons:(double)reserveFuelGallons lowFuelThresholdPercent:(double)lowFuelThresholdPercent __attribute__((swift_name("doCopy(id:year:make:model:trim:color:engine:engineDisplacementLiters:cylinderCount:tankCapacity:factoryCityMpg:factoryHwyMpg:learnedMpg:preferredFuelType:preferredFuelGrade:unitSystem:reserveFuelGallons:lowFuelThresholdPercent:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable color __attribute__((swift_name("color")));
@property (readonly) SharedLong * _Nullable cylinderCount __attribute__((swift_name("cylinderCount")));
@property (readonly) NSString *engine __attribute__((swift_name("engine")));
@property (readonly) SharedDouble * _Nullable engineDisplacementLiters __attribute__((swift_name("engineDisplacementLiters")));
@property (readonly) double factoryCityMpg __attribute__((swift_name("factoryCityMpg")));
@property (readonly) double factoryHwyMpg __attribute__((swift_name("factoryHwyMpg")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) double learnedMpg __attribute__((swift_name("learnedMpg")));
@property (readonly) double lowFuelThresholdPercent __attribute__((swift_name("lowFuelThresholdPercent")));
@property (readonly) NSString *make __attribute__((swift_name("make")));
@property (readonly) NSString *model __attribute__((swift_name("model")));
@property (readonly) NSString * _Nullable preferredFuelGrade __attribute__((swift_name("preferredFuelGrade")));
@property (readonly) SharedFuelType *preferredFuelType __attribute__((swift_name("preferredFuelType")));
@property (readonly) double reserveFuelGallons __attribute__((swift_name("reserveFuelGallons")));
@property (readonly) double tankCapacity __attribute__((swift_name("tankCapacity")));
@property (readonly) NSString * _Nullable trim __attribute__((swift_name("trim")));
@property (readonly) SharedUnitSystem *unitSystem __attribute__((swift_name("unitSystem")));
@property (readonly) int32_t year __attribute__((swift_name("year")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_iosKt")))
@interface SharedKoin_iosKt : SharedBase
@property (class, readonly) SharedKoin_coreModule *platformModule __attribute__((swift_name("platformModule")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinKt")))
@interface SharedKoinKt : SharedBase
+ (SharedKoin_coreKoinApplication *)doInitKoin __attribute__((swift_name("doInitKoin()")));
+ (SharedKoin_coreKoinApplication *)doInitKoinAppDeclaration:(void (^)(SharedKoin_coreKoinApplication *))appDeclaration __attribute__((swift_name("doInitKoin(appDeclaration:)")));
@property (class, readonly) SharedKoin_coreModule *commonModule __attribute__((swift_name("commonModule")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable(with=NormalClass(value=kotlinx/datetime/serializers/InstantIso8601Serializer))
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kotlinx_datetimeInstant")))
@interface SharedKotlinx_datetimeInstant : SharedBase <SharedKotlinComparable>
@property (class, readonly, getter=companion) SharedKotlinx_datetimeInstantCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(SharedKotlinx_datetimeInstant *)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (SharedKotlinx_datetimeInstant *)minusDuration:(int64_t)duration __attribute__((swift_name("minus(duration:)")));
- (int64_t)minusOther:(SharedKotlinx_datetimeInstant *)other __attribute__((swift_name("minus(other:)")));
- (SharedKotlinx_datetimeInstant *)plusDuration:(int64_t)duration __attribute__((swift_name("plus(duration:)")));
- (int64_t)toEpochMilliseconds __attribute__((swift_name("toEpochMilliseconds()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t epochSeconds __attribute__((swift_name("epochSeconds")));
@property (readonly) int32_t nanosecondsOfSecond __attribute__((swift_name("nanosecondsOfSecond")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface SharedKotlinEnumCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface SharedKotlinArray<T> : SharedBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(SharedInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<SharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("RuntimeCloseable")))
@protocol SharedRuntimeCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((swift_name("RuntimeSqlDriver")))
@protocol SharedRuntimeSqlDriver <SharedRuntimeCloseable>
@required
- (void)addListenerQueryKeys:(SharedKotlinArray<NSString *> *)queryKeys listener:(id<SharedRuntimeQueryListener>)listener __attribute__((swift_name("addListener(queryKeys:listener:)")));
- (SharedRuntimeTransacterTransaction * _Nullable)currentTransaction __attribute__((swift_name("currentTransaction()")));
- (id<SharedRuntimeQueryResult>)executeIdentifier:(SharedInt * _Nullable)identifier sql:(NSString *)sql parameters:(int32_t)parameters binders:(void (^ _Nullable)(id<SharedRuntimeSqlPreparedStatement>))binders __attribute__((swift_name("execute(identifier:sql:parameters:binders:)")));
- (id<SharedRuntimeQueryResult>)executeQueryIdentifier:(SharedInt * _Nullable)identifier sql:(NSString *)sql mapper:(id<SharedRuntimeQueryResult> (^)(id<SharedRuntimeSqlCursor>))mapper parameters:(int32_t)parameters binders:(void (^ _Nullable)(id<SharedRuntimeSqlPreparedStatement>))binders __attribute__((swift_name("executeQuery(identifier:sql:mapper:parameters:binders:)")));
- (id<SharedRuntimeQueryResult>)doNewTransaction __attribute__((swift_name("doNewTransaction()")));
- (void)notifyListenersQueryKeys:(SharedKotlinArray<NSString *> *)queryKeys __attribute__((swift_name("notifyListeners(queryKeys:)")));
- (void)removeListenerQueryKeys:(SharedKotlinArray<NSString *> *)queryKeys listener:(id<SharedRuntimeQueryListener>)listener __attribute__((swift_name("removeListener(queryKeys:listener:)")));
@end

__attribute__((swift_name("RuntimeTransactionCallbacks")))
@protocol SharedRuntimeTransactionCallbacks
@required
- (void)afterCommitFunction:(void (^)(void))function __attribute__((swift_name("afterCommit(function:)")));
- (void)afterRollbackFunction:(void (^)(void))function __attribute__((swift_name("afterRollback(function:)")));
@end

__attribute__((swift_name("RuntimeTransactionWithoutReturn")))
@protocol SharedRuntimeTransactionWithoutReturn <SharedRuntimeTransactionCallbacks>
@required
- (void)rollback __attribute__((swift_name("rollback()")));
- (void)transactionBody:(void (^)(id<SharedRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(body:)")));
@end

__attribute__((swift_name("RuntimeTransactionWithReturn")))
@protocol SharedRuntimeTransactionWithReturn <SharedRuntimeTransactionCallbacks>
@required
- (void)rollbackReturnValue:(id _Nullable)returnValue __attribute__((swift_name("rollback(returnValue:)")));
- (id _Nullable)transactionBody_:(id _Nullable (^)(id<SharedRuntimeTransactionWithReturn>))body __attribute__((swift_name("transaction(body_:)")));
@end

__attribute__((swift_name("RuntimeSqlSchema")))
@protocol SharedRuntimeSqlSchema
@required
- (id<SharedRuntimeQueryResult>)createDriver:(id<SharedRuntimeSqlDriver>)driver __attribute__((swift_name("create(driver:)")));
- (id<SharedRuntimeQueryResult>)migrateDriver:(id<SharedRuntimeSqlDriver>)driver oldVersion:(int64_t)oldVersion newVersion:(int64_t)newVersion callbacks:(SharedKotlinArray<SharedRuntimeAfterVersion *> *)callbacks __attribute__((swift_name("migrate(driver:oldVersion:newVersion:callbacks:)")));
@property (readonly) int64_t version __attribute__((swift_name("version")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinUnit")))
@interface SharedKotlinUnit : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)unit __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinUnit *shared __attribute__((swift_name("shared")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("RuntimeTransacterTransaction")))
@interface SharedRuntimeTransacterTransaction : SharedBase <SharedRuntimeTransactionCallbacks>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)afterCommitFunction:(void (^)(void))function __attribute__((swift_name("afterCommit(function:)")));
- (void)afterRollbackFunction:(void (^)(void))function __attribute__((swift_name("afterRollback(function:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (id<SharedRuntimeQueryResult>)endTransactionSuccessful:(BOOL)successful __attribute__((swift_name("endTransaction(successful:)")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) SharedRuntimeTransacterTransaction * _Nullable enclosingTransaction __attribute__((swift_name("enclosingTransaction")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface SharedKotlinThrowable : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (SharedKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((swift_name("RuntimeExecutableQuery")))
@interface SharedRuntimeExecutableQuery<__covariant RowType> : SharedBase
- (instancetype)initWithMapper:(RowType (^)(id<SharedRuntimeSqlCursor>))mapper __attribute__((swift_name("init(mapper:)"))) __attribute__((objc_designated_initializer));
- (id<SharedRuntimeQueryResult>)executeMapper:(id<SharedRuntimeQueryResult> (^)(id<SharedRuntimeSqlCursor>))mapper __attribute__((swift_name("execute(mapper:)")));
- (NSArray<RowType> *)executeAsList __attribute__((swift_name("executeAsList()")));
- (RowType)executeAsOne __attribute__((swift_name("executeAsOne()")));
- (RowType _Nullable)executeAsOneOrNull __attribute__((swift_name("executeAsOneOrNull()")));
@property (readonly) RowType (^mapper)(id<SharedRuntimeSqlCursor>) __attribute__((swift_name("mapper")));
@end

__attribute__((swift_name("RuntimeQuery")))
@interface SharedRuntimeQuery<__covariant RowType> : SharedRuntimeExecutableQuery<RowType>
- (instancetype)initWithMapper:(RowType (^)(id<SharedRuntimeSqlCursor>))mapper __attribute__((swift_name("init(mapper:)"))) __attribute__((objc_designated_initializer));
- (void)addListenerListener:(id<SharedRuntimeQueryListener>)listener __attribute__((swift_name("addListener(listener:)")));
- (void)removeListenerListener:(id<SharedRuntimeQueryListener>)listener __attribute__((swift_name("removeListener(listener:)")));
@end

__attribute__((swift_name("KotlinException")))
@interface SharedKotlinException : SharedKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface SharedKotlinRuntimeException : SharedKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface SharedKotlinIllegalStateException : SharedKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface SharedKotlinCancellationException : SharedKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlow")))
@protocol SharedKotlinx_coroutines_coreFlow
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<SharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinCoroutineContext")))
@protocol SharedKotlinCoroutineContext
@required
- (id _Nullable)foldInitial:(id _Nullable)initial operation:(id _Nullable (^)(id _Nullable, id<SharedKotlinCoroutineContextElement>))operation __attribute__((swift_name("fold(initial:operation:)")));
- (id<SharedKotlinCoroutineContextElement> _Nullable)getKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("get(key:)")));
- (id<SharedKotlinCoroutineContext>)minusKeyKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("minusKey(key:)")));
- (id<SharedKotlinCoroutineContext>)plusContext:(id<SharedKotlinCoroutineContext>)context __attribute__((swift_name("plus(context:)")));
@end

__attribute__((swift_name("KotlinCoroutineContextElement")))
@protocol SharedKotlinCoroutineContextElement <SharedKotlinCoroutineContext>
@required
@property (readonly) id<SharedKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinAbstractCoroutineContextElement")))
@interface SharedKotlinAbstractCoroutineContextElement : SharedBase <SharedKotlinCoroutineContextElement>
- (instancetype)initWithKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("init(key:)"))) __attribute__((objc_designated_initializer));
@property (readonly) id<SharedKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinContinuationInterceptor")))
@protocol SharedKotlinContinuationInterceptor <SharedKotlinCoroutineContextElement>
@required
- (id<SharedKotlinContinuation>)interceptContinuationContinuation:(id<SharedKotlinContinuation>)continuation __attribute__((swift_name("interceptContinuation(continuation:)")));
- (void)releaseInterceptedContinuationContinuation:(id<SharedKotlinContinuation>)continuation __attribute__((swift_name("releaseInterceptedContinuation(continuation:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineDispatcher")))
@interface SharedKotlinx_coroutines_coreCoroutineDispatcher : SharedKotlinAbstractCoroutineContextElement <SharedKotlinContinuationInterceptor>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("init(key:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) SharedKotlinx_coroutines_coreCoroutineDispatcherKey *companion __attribute__((swift_name("companion")));
- (void)dispatchContext:(id<SharedKotlinCoroutineContext>)context block:(id<SharedKotlinx_coroutines_coreRunnable>)block __attribute__((swift_name("dispatch(context:block:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (void)dispatchYieldContext:(id<SharedKotlinCoroutineContext>)context block:(id<SharedKotlinx_coroutines_coreRunnable>)block __attribute__((swift_name("dispatchYield(context:block:)")));
- (id<SharedKotlinContinuation>)interceptContinuationContinuation:(id<SharedKotlinContinuation>)continuation __attribute__((swift_name("interceptContinuation(continuation:)")));
- (BOOL)isDispatchNeededContext:(id<SharedKotlinCoroutineContext>)context __attribute__((swift_name("isDispatchNeeded(context:)")));
- (SharedKotlinx_coroutines_coreCoroutineDispatcher *)limitedParallelismParallelism:(int32_t)parallelism name:(NSString * _Nullable)name __attribute__((swift_name("limitedParallelism(parallelism:name:)")));
- (SharedKotlinx_coroutines_coreCoroutineDispatcher *)plusOther:(SharedKotlinx_coroutines_coreCoroutineDispatcher *)other __attribute__((swift_name("plus(other:)"))) __attribute__((unavailable("Operator '+' on two CoroutineDispatcher objects is meaningless. CoroutineDispatcher is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The dispatcher to the right of `+` just replaces the dispatcher to the left.")));
- (void)releaseInterceptedContinuationContinuation:(id<SharedKotlinContinuation>)continuation __attribute__((swift_name("releaseInterceptedContinuation(continuation:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinPair")))
@interface SharedKotlinPair<__covariant A, __covariant B> : SharedBase
- (instancetype)initWithFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("init(first:second:)"))) __attribute__((objc_designated_initializer));
- (SharedKotlinPair<A, B> *)doCopyFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("doCopy(first:second:)")));
- (BOOL)equalsOther:(id _Nullable)other __attribute__((swift_name("equals(other:)")));
- (int32_t)hashCode __attribute__((swift_name("hashCode()")));
- (NSString *)toString __attribute__((swift_name("toString()")));
@property (readonly) A _Nullable first __attribute__((swift_name("first")));
@property (readonly) B _Nullable second __attribute__((swift_name("second")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSharedFlow")))
@protocol SharedKotlinx_coroutines_coreSharedFlow <SharedKotlinx_coroutines_coreFlow>
@required
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreStateFlow")))
@protocol SharedKotlinx_coroutines_coreStateFlow <SharedKotlinx_coroutines_coreSharedFlow>
@required
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreModule")))
@interface SharedKoin_coreModule : SharedBase
- (instancetype)initWith_createdAtStart:(BOOL)_createdAtStart __attribute__((swift_name("init(_createdAtStart:)"))) __attribute__((objc_designated_initializer));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (SharedKoin_coreKoinDefinition<id> *)factoryQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *))definition __attribute__((swift_name("factory(qualifier:definition:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (void)includesModule:(SharedKotlinArray<SharedKoin_coreModule *> *)module __attribute__((swift_name("includes(module:)")));
- (void)includesModule_:(id)module __attribute__((swift_name("includes(module_:)")));
- (void)indexPrimaryTypeInstanceFactory:(SharedKoin_coreInstanceFactory<id> *)instanceFactory __attribute__((swift_name("indexPrimaryType(instanceFactory:)")));
- (void)indexSecondaryTypesInstanceFactory:(SharedKoin_coreInstanceFactory<id> *)instanceFactory __attribute__((swift_name("indexSecondaryTypes(instanceFactory:)")));
- (NSArray<SharedKoin_coreModule *> *)plusModules:(NSArray<SharedKoin_coreModule *> *)modules __attribute__((swift_name("plus(modules:)")));
- (NSArray<SharedKoin_coreModule *> *)plusModule:(SharedKoin_coreModule *)module __attribute__((swift_name("plus(module:)")));
- (void)prepareForCreationAtStartInstanceFactory:(SharedKoin_coreSingleInstanceFactory<id> *)instanceFactory __attribute__((swift_name("prepareForCreationAtStart(instanceFactory:)")));
- (void)scopeScopeSet:(void (^)(SharedKoin_coreScopeDSL *))scopeSet __attribute__((swift_name("scope(scopeSet:)")));
- (void)scopeQualifier:(id<SharedKoin_coreQualifier>)qualifier scopeSet:(void (^)(SharedKoin_coreScopeDSL *))scopeSet __attribute__((swift_name("scope(qualifier:scopeSet:)")));
- (SharedKoin_coreKoinDefinition<id> *)singleQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier createdAtStart:(BOOL)createdAtStart definition:(id _Nullable (^)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *))definition __attribute__((swift_name("single(qualifier:createdAtStart:definition:)")));
@property (readonly) SharedMutableSet<SharedKoin_coreSingleInstanceFactory<id> *> *eagerInstances __attribute__((swift_name("eagerInstances")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSMutableArray<SharedKoin_coreModule *> *includedModules __attribute__((swift_name("includedModules")));
@property (readonly) BOOL isLoaded __attribute__((swift_name("isLoaded")));
@property (readonly) SharedMutableDictionary<NSString *, SharedKoin_coreInstanceFactory<id> *> *mappings __attribute__((swift_name("mappings")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreKoinApplication")))
@interface SharedKoin_coreKoinApplication : SharedBase
@property (class, readonly, getter=companion) SharedKoin_coreKoinApplicationCompanion *companion __attribute__((swift_name("companion")));
- (void)allowOverrideOverride:(BOOL)override __attribute__((swift_name("allowOverride(override:)")));
- (void)close __attribute__((swift_name("close()")));
- (void)createEagerInstances __attribute__((swift_name("createEagerInstances()")));
- (SharedKoin_coreKoinApplication *)loggerLogger:(SharedKoin_coreLogger *)logger __attribute__((swift_name("logger(logger:)")));
- (SharedKoin_coreKoinApplication *)modulesModules:(SharedKotlinArray<SharedKoin_coreModule *> *)modules __attribute__((swift_name("modules(modules:)")));
- (SharedKoin_coreKoinApplication *)modulesModules_:(NSArray<SharedKoin_coreModule *> *)modules __attribute__((swift_name("modules(modules_:)")));
- (SharedKoin_coreKoinApplication *)modulesModules__:(SharedKoin_coreModule *)modules __attribute__((swift_name("modules(modules__:)")));
- (SharedKoin_coreKoinApplication *)printLoggerLevel:(SharedKoin_coreLevel *)level __attribute__((swift_name("printLogger(level:)")));
- (SharedKoin_coreKoinApplication *)propertiesValues:(NSDictionary<NSString *, id> *)values __attribute__((swift_name("properties(values:)")));
@property (readonly) SharedKoin_coreKoin *koin __attribute__((swift_name("koin")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kotlinx_datetimeInstant.Companion")))
@interface SharedKotlinx_datetimeInstantCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinx_datetimeInstantCompanion *shared __attribute__((swift_name("shared")));
- (SharedKotlinx_datetimeInstant *)fromEpochMillisecondsEpochMilliseconds:(int64_t)epochMilliseconds __attribute__((swift_name("fromEpochMilliseconds(epochMilliseconds:)")));
- (SharedKotlinx_datetimeInstant *)fromEpochSecondsEpochSeconds:(int64_t)epochSeconds nanosecondAdjustment:(int32_t)nanosecondAdjustment __attribute__((swift_name("fromEpochSeconds(epochSeconds:nanosecondAdjustment:)")));
- (SharedKotlinx_datetimeInstant *)fromEpochSecondsEpochSeconds:(int64_t)epochSeconds nanosecondAdjustment_:(int64_t)nanosecondAdjustment __attribute__((swift_name("fromEpochSeconds(epochSeconds:nanosecondAdjustment_:)")));
- (SharedKotlinx_datetimeInstant *)now __attribute__((swift_name("now()"))) __attribute__((unavailable("Use Clock.System.now() instead")));
- (SharedKotlinx_datetimeInstant *)parseInput:(id)input format:(id<SharedKotlinx_datetimeDateTimeFormat>)format __attribute__((swift_name("parse(input:format:)")));
- (id<SharedKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@property (readonly) SharedKotlinx_datetimeInstant *DISTANT_FUTURE __attribute__((swift_name("DISTANT_FUTURE")));
@property (readonly) SharedKotlinx_datetimeInstant *DISTANT_PAST __attribute__((swift_name("DISTANT_PAST")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol SharedKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end

__attribute__((swift_name("RuntimeQueryListener")))
@protocol SharedRuntimeQueryListener
@required
- (void)queryResultsChanged __attribute__((swift_name("queryResultsChanged()")));
@end

__attribute__((swift_name("RuntimeQueryResult")))
@protocol SharedRuntimeQueryResult
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)awaitWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("await(completionHandler:)")));
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("RuntimeSqlPreparedStatement")))
@protocol SharedRuntimeSqlPreparedStatement
@required
- (void)bindBooleanIndex:(int32_t)index boolean:(SharedBoolean * _Nullable)boolean __attribute__((swift_name("bindBoolean(index:boolean:)")));
- (void)bindBytesIndex:(int32_t)index bytes:(SharedKotlinByteArray * _Nullable)bytes __attribute__((swift_name("bindBytes(index:bytes:)")));
- (void)bindDoubleIndex:(int32_t)index double:(SharedDouble * _Nullable)double_ __attribute__((swift_name("bindDouble(index:double:)")));
- (void)bindLongIndex:(int32_t)index long:(SharedLong * _Nullable)long_ __attribute__((swift_name("bindLong(index:long:)")));
- (void)bindStringIndex:(int32_t)index string:(NSString * _Nullable)string __attribute__((swift_name("bindString(index:string:)")));
@end

__attribute__((swift_name("RuntimeSqlCursor")))
@protocol SharedRuntimeSqlCursor
@required
- (SharedBoolean * _Nullable)getBooleanIndex:(int32_t)index __attribute__((swift_name("getBoolean(index:)")));
- (SharedKotlinByteArray * _Nullable)getBytesIndex:(int32_t)index __attribute__((swift_name("getBytes(index:)")));
- (SharedDouble * _Nullable)getDoubleIndex:(int32_t)index __attribute__((swift_name("getDouble(index:)")));
- (SharedLong * _Nullable)getLongIndex:(int32_t)index __attribute__((swift_name("getLong(index:)")));
- (NSString * _Nullable)getStringIndex:(int32_t)index __attribute__((swift_name("getString(index:)")));
- (id<SharedRuntimeQueryResult>)next __attribute__((swift_name("next()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("RuntimeAfterVersion")))
@interface SharedRuntimeAfterVersion : SharedBase
- (instancetype)initWithAfterVersion:(int64_t)afterVersion block:(void (^)(id<SharedRuntimeSqlDriver>))block __attribute__((swift_name("init(afterVersion:block:)"))) __attribute__((objc_designated_initializer));
@property (readonly) int64_t afterVersion __attribute__((swift_name("afterVersion")));
@property (readonly) void (^block)(id<SharedRuntimeSqlDriver>) __attribute__((swift_name("block")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlowCollector")))
@protocol SharedKotlinx_coroutines_coreFlowCollector
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(id _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));
@end

__attribute__((swift_name("KotlinCoroutineContextKey")))
@protocol SharedKotlinCoroutineContextKey
@required
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinContinuation")))
@protocol SharedKotlinContinuation
@required
- (void)resumeWithResult:(id _Nullable)result __attribute__((swift_name("resumeWith(result:)")));
@property (readonly) id<SharedKotlinCoroutineContext> context __attribute__((swift_name("context")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
 *   kotlin.ExperimentalStdlibApi
*/
__attribute__((swift_name("KotlinAbstractCoroutineContextKey")))
@interface SharedKotlinAbstractCoroutineContextKey<B, E> : SharedBase <SharedKotlinCoroutineContextKey>
- (instancetype)initWithBaseKey:(id<SharedKotlinCoroutineContextKey>)baseKey safeCast:(E _Nullable (^)(id<SharedKotlinCoroutineContextElement>))safeCast __attribute__((swift_name("init(baseKey:safeCast:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.ExperimentalStdlibApi
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineDispatcher.Key")))
@interface SharedKotlinx_coroutines_coreCoroutineDispatcherKey : SharedKotlinAbstractCoroutineContextKey<id<SharedKotlinContinuationInterceptor>, SharedKotlinx_coroutines_coreCoroutineDispatcher *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithBaseKey:(id<SharedKotlinCoroutineContextKey>)baseKey safeCast:(id<SharedKotlinCoroutineContextElement> _Nullable (^)(id<SharedKotlinCoroutineContextElement>))safeCast __attribute__((swift_name("init(baseKey:safeCast:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)key __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinx_coroutines_coreCoroutineDispatcherKey *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreRunnable")))
@protocol SharedKotlinx_coroutines_coreRunnable
@required
- (void)run __attribute__((swift_name("run()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreKoinDefinition")))
@interface SharedKoin_coreKoinDefinition<R> : SharedBase
- (instancetype)initWithModule:(SharedKoin_coreModule *)module factory:(SharedKoin_coreInstanceFactory<R> *)factory __attribute__((swift_name("init(module:factory:)"))) __attribute__((objc_designated_initializer));
- (SharedKoin_coreKoinDefinition<R> *)doCopyModule:(SharedKoin_coreModule *)module factory:(SharedKoin_coreInstanceFactory<R> *)factory __attribute__((swift_name("doCopy(module:factory:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKoin_coreInstanceFactory<R> *factory __attribute__((swift_name("factory")));
@property (readonly) SharedKoin_coreModule *module __attribute__((swift_name("module")));
@end

__attribute__((swift_name("Koin_coreQualifier")))
@protocol SharedKoin_coreQualifier
@required
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("Koin_coreLockable")))
@interface SharedKoin_coreLockable : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreScope")))
@interface SharedKoin_coreScope : SharedKoin_coreLockable
- (instancetype)initWithScopeQualifier:(id<SharedKoin_coreQualifier>)scopeQualifier id:(NSString *)id isRoot:(BOOL)isRoot _koin:(SharedKoin_coreKoin *)_koin __attribute__((swift_name("init(scopeQualifier:id:isRoot:_koin:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (void)close __attribute__((swift_name("close()")));
- (void)declareInstance:(id _Nullable)instance qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes allowOverride:(BOOL)allowOverride __attribute__((swift_name("declare(instance:qualifier:secondaryTypes:allowOverride:)")));
- (id)getQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(qualifier:parameters:)")));
- (id _Nullable)getClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(clazz:qualifier:parameters:)")));
- (NSArray<id> *)getAll __attribute__((swift_name("getAll()")));
- (NSArray<id> *)getAllClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("getAll(clazz:)")));
- (SharedKoin_coreKoin *)getKoin __attribute__((swift_name("getKoin()")));
- (id _Nullable)getOrNullQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(qualifier:parameters:)")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(clazz:qualifier:parameters:)")));
- (id)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (id)getPropertyKey:(NSString *)key defaultValue:(id)defaultValue __attribute__((swift_name("getProperty(key:defaultValue:)")));
- (id _Nullable)getPropertyOrNullKey:(NSString *)key __attribute__((swift_name("getPropertyOrNull(key:)")));
- (SharedKoin_coreScope *)getScopeScopeID:(NSString *)scopeID __attribute__((swift_name("getScope(scopeID:)")));
- (id _Nullable)getSource __attribute__((swift_name("getSource()")));
- (id<SharedKotlinLazy>)injectQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("inject(qualifier:mode:parameters:)")));
- (id<SharedKotlinLazy>)injectOrNullQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("injectOrNull(qualifier:mode:parameters:)")));
- (BOOL)isNotClosed __attribute__((swift_name("isNotClosed()")));
- (void)linkToScopes:(SharedKotlinArray<SharedKoin_coreScope *> *)scopes __attribute__((swift_name("linkTo(scopes:)")));
- (void)registerCallbackCallback:(id<SharedKoin_coreScopeCallback>)callback __attribute__((swift_name("registerCallback(callback:)")));
- (NSString *)description __attribute__((swift_name("description()")));
- (void)unlinkScopes:(SharedKotlinArray<SharedKoin_coreScope *> *)scopes __attribute__((swift_name("unlink(scopes:)")));
@property (readonly) BOOL closed __attribute__((swift_name("closed")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) BOOL isRoot __attribute__((swift_name("isRoot")));
@property (readonly) SharedKoin_coreLogger *logger __attribute__((swift_name("logger")));
@property (readonly) id<SharedKoin_coreQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@property id _Nullable sourceValue __attribute__((swift_name("sourceValue")));
@end

__attribute__((swift_name("Koin_coreParametersHolder")))
@interface SharedKoin_coreParametersHolder : SharedBase
- (instancetype)initWith_values:(NSMutableArray<id> *)_values useIndexedValues:(SharedBoolean * _Nullable)useIndexedValues __attribute__((swift_name("init(_values:useIndexedValues:)"))) __attribute__((objc_designated_initializer));
- (SharedKoin_coreParametersHolder *)addValue:(id)value __attribute__((swift_name("add(value:)")));
- (id _Nullable)component1 __attribute__((swift_name("component1()")));
- (id _Nullable)component2 __attribute__((swift_name("component2()")));
- (id _Nullable)component3 __attribute__((swift_name("component3()")));
- (id _Nullable)component4 __attribute__((swift_name("component4()")));
- (id _Nullable)component5 __attribute__((swift_name("component5()")));
- (id _Nullable)elementAtI:(int32_t)i clazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("elementAt(i:clazz:)")));
- (id)get __attribute__((swift_name("get()")));
- (id _Nullable)getI:(int32_t)i __attribute__((swift_name("get(i:)")));
- (id _Nullable)getOrNull __attribute__((swift_name("getOrNull()")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("getOrNull(clazz:)")));
- (SharedKoin_coreParametersHolder *)insertIndex:(int32_t)index value:(id)value __attribute__((swift_name("insert(index:value:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (BOOL)isNotEmpty __attribute__((swift_name("isNotEmpty()")));
- (void)setI:(int32_t)i t:(id _Nullable)t __attribute__((swift_name("set(i:t:)")));
- (int32_t)size __attribute__((swift_name("size()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property int32_t index __attribute__((swift_name("index")));
@property (readonly) SharedBoolean * _Nullable useIndexedValues __attribute__((swift_name("useIndexedValues")));
@property (readonly) NSArray<id> *values __attribute__((swift_name("values")));
@end

__attribute__((swift_name("Koin_coreInstanceFactory")))
@interface SharedKoin_coreInstanceFactory<T> : SharedKoin_coreLockable
- (instancetype)initWithBeanDefinition:(SharedKoin_coreBeanDefinition<T> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
@property (class, readonly, getter=companion) SharedKoin_coreInstanceFactoryCompanion *companion __attribute__((swift_name("companion")));
- (T _Nullable)createContext:(SharedKoin_coreResolutionContext *)context __attribute__((swift_name("create(context:)")));
- (void)dropScope:(SharedKoin_coreScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedKoin_coreResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedKoin_coreResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
@property (readonly) SharedKoin_coreBeanDefinition<T> *beanDefinition __attribute__((swift_name("beanDefinition")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreSingleInstanceFactory")))
@interface SharedKoin_coreSingleInstanceFactory<T> : SharedKoin_coreInstanceFactory<T>
- (instancetype)initWithBeanDefinition:(SharedKoin_coreBeanDefinition<T> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer));
- (T _Nullable)createContext:(SharedKoin_coreResolutionContext *)context __attribute__((swift_name("create(context:)")));
- (void)dropScope:(SharedKoin_coreScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedKoin_coreResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedKoin_coreResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreScopeDSL")))
@interface SharedKoin_coreScopeDSL : SharedBase
- (instancetype)initWithScopeQualifier:(id<SharedKoin_coreQualifier>)scopeQualifier module:(SharedKoin_coreModule *)module __attribute__((swift_name("init(scopeQualifier:module:)"))) __attribute__((objc_designated_initializer));
- (SharedKoin_coreKoinDefinition<id> *)factoryQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *))definition __attribute__((swift_name("factory(qualifier:definition:)")));
- (SharedKoin_coreKoinDefinition<id> *)scopedQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *))definition __attribute__((swift_name("scoped(qualifier:definition:)")));
@property (readonly) SharedKoin_coreModule *module __attribute__((swift_name("module")));
@property (readonly) id<SharedKoin_coreQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreKoinApplication.Companion")))
@interface SharedKoin_coreKoinApplicationCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoin_coreKoinApplicationCompanion *shared __attribute__((swift_name("shared")));
- (SharedKoin_coreKoinApplication *)doInit __attribute__((swift_name("doInit()")));
@end

__attribute__((swift_name("Koin_coreLogger")))
@interface SharedKoin_coreLogger : SharedBase
- (instancetype)initWithLevel:(SharedKoin_coreLevel *)level __attribute__((swift_name("init(level:)"))) __attribute__((objc_designated_initializer));
- (void)debugMsg:(NSString *)msg __attribute__((swift_name("debug(msg:)")));
- (void)displayLevel:(SharedKoin_coreLevel *)level msg:(NSString *)msg __attribute__((swift_name("display(level:msg:)")));
- (void)errorMsg:(NSString *)msg __attribute__((swift_name("error(msg:)")));
- (void)infoMsg:(NSString *)msg __attribute__((swift_name("info(msg:)")));
- (BOOL)isAtLvl:(SharedKoin_coreLevel *)lvl __attribute__((swift_name("isAt(lvl:)")));
- (void)logLvl:(SharedKoin_coreLevel *)lvl msg:(NSString *(^)(void))msg __attribute__((swift_name("log(lvl:msg:)")));
- (void)logLvl:(SharedKoin_coreLevel *)lvl msg_:(NSString *)msg __attribute__((swift_name("log(lvl:msg_:)")));
- (void)warnMsg:(NSString *)msg __attribute__((swift_name("warn(msg:)")));
@property SharedKoin_coreLevel *level __attribute__((swift_name("level")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreLevel")))
@interface SharedKoin_coreLevel : SharedKotlinEnum<SharedKoin_coreLevel *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKoin_coreLevel *debug __attribute__((swift_name("debug")));
@property (class, readonly) SharedKoin_coreLevel *info __attribute__((swift_name("info")));
@property (class, readonly) SharedKoin_coreLevel *warning __attribute__((swift_name("warning")));
@property (class, readonly) SharedKoin_coreLevel *error __attribute__((swift_name("error")));
@property (class, readonly) SharedKoin_coreLevel *none __attribute__((swift_name("none")));
+ (SharedKotlinArray<SharedKoin_coreLevel *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKoin_coreLevel *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreKoin")))
@interface SharedKoin_coreKoin : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)close __attribute__((swift_name("close()")));
- (void)createEagerInstances __attribute__((swift_name("createEagerInstances()")));
- (SharedKoin_coreScope *)createScopeT:(id<SharedKoin_coreKoinScopeComponent>)t __attribute__((swift_name("createScope(t:)")));
- (SharedKoin_coreScope *)createScopeScopeId:(NSString *)scopeId __attribute__((swift_name("createScope(scopeId:)")));
- (SharedKoin_coreScope *)createScopeScopeId:(NSString *)scopeId source:(id _Nullable)source __attribute__((swift_name("createScope(scopeId:source:)")));
- (SharedKoin_coreScope *)createScopeScopeId:(NSString *)scopeId qualifier:(id<SharedKoin_coreQualifier>)qualifier source:(id _Nullable)source __attribute__((swift_name("createScope(scopeId:qualifier:source:)")));
- (void)declareInstance:(id _Nullable)instance qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes allowOverride:(BOOL)allowOverride __attribute__((swift_name("declare(instance:qualifier:secondaryTypes:allowOverride:)")));
- (void)deletePropertyKey:(NSString *)key __attribute__((swift_name("deleteProperty(key:)")));
- (void)deleteScopeScopeId:(NSString *)scopeId __attribute__((swift_name("deleteScope(scopeId:)")));
- (id)getQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(qualifier:parameters:)")));
- (id _Nullable)getClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(clazz:qualifier:parameters:)")));
- (NSArray<id> *)getAll __attribute__((swift_name("getAll()")));
- (SharedKoin_coreScope *)getOrCreateScopeScopeId:(NSString *)scopeId __attribute__((swift_name("getOrCreateScope(scopeId:)")));
- (SharedKoin_coreScope *)getOrCreateScopeScopeId:(NSString *)scopeId qualifier:(id<SharedKoin_coreQualifier>)qualifier source:(id _Nullable)source __attribute__((swift_name("getOrCreateScope(scopeId:qualifier:source:)")));
- (id _Nullable)getOrNullQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(qualifier:parameters:)")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(clazz:qualifier:parameters:)")));
- (id _Nullable)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (id)getPropertyKey:(NSString *)key defaultValue:(id)defaultValue __attribute__((swift_name("getProperty(key:defaultValue:)")));
- (SharedKoin_coreScope *)getScopeScopeId:(NSString *)scopeId __attribute__((swift_name("getScope(scopeId:)")));
- (SharedKoin_coreScope * _Nullable)getScopeOrNullScopeId:(NSString *)scopeId __attribute__((swift_name("getScopeOrNull(scopeId:)")));
- (id<SharedKotlinLazy>)injectQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("inject(qualifier:mode:parameters:)")));
- (id<SharedKotlinLazy>)injectOrNullQualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedKoin_coreParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("injectOrNull(qualifier:mode:parameters:)")));
- (void)loadModulesModules:(NSArray<SharedKoin_coreModule *> *)modules allowOverride:(BOOL)allowOverride createEagerInstances:(BOOL)createEagerInstances __attribute__((swift_name("loadModules(modules:allowOverride:createEagerInstances:)")));
- (void)setPropertyKey:(NSString *)key value:(id)value __attribute__((swift_name("setProperty(key:value:)")));
- (void)setupLoggerLogger:(SharedKoin_coreLogger *)logger __attribute__((swift_name("setupLogger(logger:)")));
- (void)unloadModulesModules:(NSArray<SharedKoin_coreModule *> *)modules __attribute__((swift_name("unloadModules(modules:)")));
@property (readonly) SharedKoin_coreExtensionManager *extensionManager __attribute__((swift_name("extensionManager")));
@property (readonly) SharedKoin_coreInstanceRegistry *instanceRegistry __attribute__((swift_name("instanceRegistry")));
@property (readonly) SharedKoin_coreLogger *logger __attribute__((swift_name("logger")));
@property (readonly) SharedKoin_corePropertyRegistry *propertyRegistry __attribute__((swift_name("propertyRegistry")));
@property (readonly) SharedKoin_coreScopeRegistry *scopeRegistry __attribute__((swift_name("scopeRegistry")));
@end

__attribute__((swift_name("Kotlinx_datetimeDateTimeFormat")))
@protocol SharedKotlinx_datetimeDateTimeFormat
@required
- (NSString *)formatValue:(id _Nullable)value __attribute__((swift_name("format(value:)")));
- (id<SharedKotlinAppendable>)formatToAppendable:(id<SharedKotlinAppendable>)appendable value:(id _Nullable)value __attribute__((swift_name("formatTo(appendable:value:)")));
- (id _Nullable)parseInput:(id)input __attribute__((swift_name("parse(input:)")));
- (id _Nullable)parseOrNullInput:(id)input __attribute__((swift_name("parseOrNull(input:)")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerializationStrategy")))
@protocol SharedKotlinx_serialization_coreSerializationStrategy
@required
- (void)serializeEncoder:(id<SharedKotlinx_serialization_coreEncoder>)encoder value:(id _Nullable)value __attribute__((swift_name("serialize(encoder:value:)")));
@property (readonly) id<SharedKotlinx_serialization_coreSerialDescriptor> descriptor __attribute__((swift_name("descriptor")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreDeserializationStrategy")))
@protocol SharedKotlinx_serialization_coreDeserializationStrategy
@required
- (id _Nullable)deserializeDecoder:(id<SharedKotlinx_serialization_coreDecoder>)decoder __attribute__((swift_name("deserialize(decoder:)")));
@property (readonly) id<SharedKotlinx_serialization_coreSerialDescriptor> descriptor __attribute__((swift_name("descriptor")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreKSerializer")))
@protocol SharedKotlinx_serialization_coreKSerializer <SharedKotlinx_serialization_coreSerializationStrategy, SharedKotlinx_serialization_coreDeserializationStrategy>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinByteArray")))
@interface SharedKotlinByteArray : SharedBase
+ (instancetype)arrayWithSize:(int32_t)size __attribute__((swift_name("init(size:)")));
+ (instancetype)arrayWithSize:(int32_t)size init:(SharedByte *(^)(SharedInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (int8_t)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (SharedKotlinByteIterator *)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(int8_t)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("KotlinKDeclarationContainer")))
@protocol SharedKotlinKDeclarationContainer
@required
@end

__attribute__((swift_name("KotlinKAnnotatedElement")))
@protocol SharedKotlinKAnnotatedElement
@required
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((swift_name("KotlinKClassifier")))
@protocol SharedKotlinKClassifier
@required
@end

__attribute__((swift_name("KotlinKClass")))
@protocol SharedKotlinKClass <SharedKotlinKDeclarationContainer, SharedKotlinKAnnotatedElement, SharedKotlinKClassifier>
@required

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
- (BOOL)isInstanceValue:(id _Nullable)value __attribute__((swift_name("isInstance(value:)")));
@property (readonly) NSString * _Nullable qualifiedName __attribute__((swift_name("qualifiedName")));
@property (readonly) NSString * _Nullable simpleName __attribute__((swift_name("simpleName")));
@end

__attribute__((swift_name("KotlinLazy")))
@protocol SharedKotlinLazy
@required
- (BOOL)isInitialized __attribute__((swift_name("isInitialized()")));
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinLazyThreadSafetyMode")))
@interface SharedKotlinLazyThreadSafetyMode : SharedKotlinEnum<SharedKotlinLazyThreadSafetyMode *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *synchronized __attribute__((swift_name("synchronized")));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *publication __attribute__((swift_name("publication")));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *none __attribute__((swift_name("none")));
+ (SharedKotlinArray<SharedKotlinLazyThreadSafetyMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKotlinLazyThreadSafetyMode *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("Koin_coreScopeCallback")))
@protocol SharedKoin_coreScopeCallback
@required
- (void)onScopeCloseScope:(SharedKoin_coreScope *)scope __attribute__((swift_name("onScopeClose(scope:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreBeanDefinition")))
@interface SharedKoin_coreBeanDefinition<T> : SharedBase
- (instancetype)initWithScopeQualifier:(id<SharedKoin_coreQualifier>)scopeQualifier primaryType:(id<SharedKotlinKClass>)primaryType qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier definition:(T _Nullable (^)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *))definition kind:(SharedKoin_coreKind *)kind secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes __attribute__((swift_name("init(scopeQualifier:primaryType:qualifier:definition:kind:secondaryTypes:)"))) __attribute__((objc_designated_initializer));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (BOOL)hasTypeClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("hasType(clazz:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier scopeDefinition:(id<SharedKoin_coreQualifier>)scopeDefinition __attribute__((swift_name("is(clazz:qualifier:scopeDefinition:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property SharedKoin_coreCallbacks<T> *callbacks __attribute__((swift_name("callbacks")));
@property (readonly) T _Nullable (^definition)(SharedKoin_coreScope *, SharedKoin_coreParametersHolder *) __attribute__((swift_name("definition")));
@property (readonly) SharedKoin_coreKind *kind __attribute__((swift_name("kind")));
@property (readonly) id<SharedKotlinKClass> primaryType __attribute__((swift_name("primaryType")));
@property id<SharedKoin_coreQualifier> _Nullable qualifier __attribute__((swift_name("qualifier")));
@property (readonly) id<SharedKoin_coreQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@property NSArray<id<SharedKotlinKClass>> *secondaryTypes __attribute__((swift_name("secondaryTypes")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreInstanceFactoryCompanion")))
@interface SharedKoin_coreInstanceFactoryCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoin_coreInstanceFactoryCompanion *shared __attribute__((swift_name("shared")));
@property (readonly) NSString *ERROR_SEPARATOR __attribute__((swift_name("ERROR_SEPARATOR")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreResolutionContext")))
@interface SharedKoin_coreResolutionContext : SharedBase
- (instancetype)initWithLogger:(SharedKoin_coreLogger *)logger scope:(SharedKoin_coreScope *)scope clazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedKoin_coreQualifier> _Nullable)qualifier parameters:(SharedKoin_coreParametersHolder * _Nullable)parameters __attribute__((swift_name("init(logger:scope:clazz:qualifier:parameters:)"))) __attribute__((objc_designated_initializer));
@property (readonly) id<SharedKotlinKClass> clazz __attribute__((swift_name("clazz")));
@property (readonly) NSString *debugTag __attribute__((swift_name("debugTag")));
@property (readonly) SharedKoin_coreLogger *logger __attribute__((swift_name("logger")));
@property (readonly) SharedKoin_coreParametersHolder * _Nullable parameters __attribute__((swift_name("parameters")));
@property (readonly) id<SharedKoin_coreQualifier> _Nullable qualifier __attribute__((swift_name("qualifier")));
@property (readonly) SharedKoin_coreScope *scope __attribute__((swift_name("scope")));
@end

__attribute__((swift_name("Koin_coreKoinComponent")))
@protocol SharedKoin_coreKoinComponent
@required
- (SharedKoin_coreKoin *)getKoin __attribute__((swift_name("getKoin()")));
@end

__attribute__((swift_name("Koin_coreKoinScopeComponent")))
@protocol SharedKoin_coreKoinScopeComponent <SharedKoin_coreKoinComponent>
@required
@property (readonly) SharedKoin_coreScope *scope __attribute__((swift_name("scope")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreExtensionManager")))
@interface SharedKoin_coreExtensionManager : SharedBase
- (instancetype)initWith_koin:(SharedKoin_coreKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)close __attribute__((swift_name("close()")));
- (id<SharedKoin_coreKoinExtension>)getExtensionId:(NSString *)id __attribute__((swift_name("getExtension(id:)")));
- (id<SharedKoin_coreKoinExtension> _Nullable)getExtensionOrNullId:(NSString *)id __attribute__((swift_name("getExtensionOrNull(id:)")));
- (void)registerExtensionId:(NSString *)id extension:(id<SharedKoin_coreKoinExtension>)extension __attribute__((swift_name("registerExtension(id:extension:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreInstanceRegistry")))
@interface SharedKoin_coreInstanceRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin_coreKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)saveMappingAllowOverride:(BOOL)allowOverride mapping:(NSString *)mapping factory:(SharedKoin_coreInstanceFactory<id> *)factory logWarning:(BOOL)logWarning __attribute__((swift_name("saveMapping(allowOverride:mapping:factory:logWarning:)")));
- (int32_t)size __attribute__((swift_name("size()")));
@property (readonly) SharedKoin_coreKoin *_koin __attribute__((swift_name("_koin")));
@property (readonly) NSDictionary<NSString *, SharedKoin_coreInstanceFactory<id> *> *instances __attribute__((swift_name("instances")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_corePropertyRegistry")))
@interface SharedKoin_corePropertyRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin_coreKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)close __attribute__((swift_name("close()")));
- (void)deletePropertyKey:(NSString *)key __attribute__((swift_name("deleteProperty(key:)")));
- (id _Nullable)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (void)savePropertiesProperties:(NSDictionary<NSString *, id> *)properties __attribute__((swift_name("saveProperties(properties:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreScopeRegistry")))
@interface SharedKoin_coreScopeRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin_coreKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKoin_coreScopeRegistryCompanion *companion __attribute__((swift_name("companion")));
- (void)loadScopesModules:(NSSet<SharedKoin_coreModule *> *)modules __attribute__((swift_name("loadScopes(modules:)")));
@property (readonly) SharedKoin_coreScope *rootScope __attribute__((swift_name("rootScope")));
@property (readonly) NSSet<id<SharedKoin_coreQualifier>> *scopeDefinitions __attribute__((swift_name("scopeDefinitions")));
@end

__attribute__((swift_name("KotlinAppendable")))
@protocol SharedKotlinAppendable
@required
- (id<SharedKotlinAppendable>)appendValue:(unichar)value __attribute__((swift_name("append(value:)")));
- (id<SharedKotlinAppendable>)appendValue_:(id _Nullable)value __attribute__((swift_name("append(value_:)")));
- (id<SharedKotlinAppendable>)appendValue:(id _Nullable)value startIndex:(int32_t)startIndex endIndex:(int32_t)endIndex __attribute__((swift_name("append(value:startIndex:endIndex:)")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreEncoder")))
@protocol SharedKotlinx_serialization_coreEncoder
@required
- (id<SharedKotlinx_serialization_coreCompositeEncoder>)beginCollectionDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor collectionSize:(int32_t)collectionSize __attribute__((swift_name("beginCollection(descriptor:collectionSize:)")));
- (id<SharedKotlinx_serialization_coreCompositeEncoder>)beginStructureDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("beginStructure(descriptor:)")));
- (void)encodeBooleanValue:(BOOL)value __attribute__((swift_name("encodeBoolean(value:)")));
- (void)encodeByteValue:(int8_t)value __attribute__((swift_name("encodeByte(value:)")));
- (void)encodeCharValue:(unichar)value __attribute__((swift_name("encodeChar(value:)")));
- (void)encodeDoubleValue:(double)value __attribute__((swift_name("encodeDouble(value:)")));
- (void)encodeEnumEnumDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)enumDescriptor index:(int32_t)index __attribute__((swift_name("encodeEnum(enumDescriptor:index:)")));
- (void)encodeFloatValue:(float)value __attribute__((swift_name("encodeFloat(value:)")));
- (id<SharedKotlinx_serialization_coreEncoder>)encodeInlineDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("encodeInline(descriptor:)")));
- (void)encodeIntValue:(int32_t)value __attribute__((swift_name("encodeInt(value:)")));
- (void)encodeLongValue:(int64_t)value __attribute__((swift_name("encodeLong(value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNotNullMark __attribute__((swift_name("encodeNotNullMark()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNull __attribute__((swift_name("encodeNull()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNullableSerializableValueSerializer:(id<SharedKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeNullableSerializableValue(serializer:value:)")));
- (void)encodeSerializableValueSerializer:(id<SharedKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeSerializableValue(serializer:value:)")));
- (void)encodeShortValue:(int16_t)value __attribute__((swift_name("encodeShort(value:)")));
- (void)encodeStringValue:(NSString *)value __attribute__((swift_name("encodeString(value:)")));
@property (readonly) SharedKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerialDescriptor")))
@protocol SharedKotlinx_serialization_coreSerialDescriptor
@required

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (NSArray<id<SharedKotlinAnnotation>> *)getElementAnnotationsIndex:(int32_t)index __attribute__((swift_name("getElementAnnotations(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<SharedKotlinx_serialization_coreSerialDescriptor>)getElementDescriptorIndex:(int32_t)index __attribute__((swift_name("getElementDescriptor(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (int32_t)getElementIndexName:(NSString *)name __attribute__((swift_name("getElementIndex(name:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (NSString *)getElementNameIndex:(int32_t)index __attribute__((swift_name("getElementName(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)isElementOptionalIndex:(int32_t)index __attribute__((swift_name("isElementOptional(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) NSArray<id<SharedKotlinAnnotation>> *annotations __attribute__((swift_name("annotations")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) int32_t elementsCount __attribute__((swift_name("elementsCount")));
@property (readonly) BOOL isInline __attribute__((swift_name("isInline")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) BOOL isNullable __attribute__((swift_name("isNullable")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) SharedKotlinx_serialization_coreSerialKind *kind __attribute__((swift_name("kind")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) NSString *serialName __attribute__((swift_name("serialName")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreDecoder")))
@protocol SharedKotlinx_serialization_coreDecoder
@required
- (id<SharedKotlinx_serialization_coreCompositeDecoder>)beginStructureDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("beginStructure(descriptor:)")));
- (BOOL)decodeBoolean __attribute__((swift_name("decodeBoolean()")));
- (int8_t)decodeByte __attribute__((swift_name("decodeByte()")));
- (unichar)decodeChar __attribute__((swift_name("decodeChar()")));
- (double)decodeDouble __attribute__((swift_name("decodeDouble()")));
- (int32_t)decodeEnumEnumDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)enumDescriptor __attribute__((swift_name("decodeEnum(enumDescriptor:)")));
- (float)decodeFloat __attribute__((swift_name("decodeFloat()")));
- (id<SharedKotlinx_serialization_coreDecoder>)decodeInlineDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeInline(descriptor:)")));
- (int32_t)decodeInt __attribute__((swift_name("decodeInt()")));
- (int64_t)decodeLong __attribute__((swift_name("decodeLong()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)decodeNotNullMark __attribute__((swift_name("decodeNotNullMark()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (SharedKotlinNothing * _Nullable)decodeNull __attribute__((swift_name("decodeNull()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id _Nullable)decodeNullableSerializableValueDeserializer:(id<SharedKotlinx_serialization_coreDeserializationStrategy>)deserializer __attribute__((swift_name("decodeNullableSerializableValue(deserializer:)")));
- (id _Nullable)decodeSerializableValueDeserializer:(id<SharedKotlinx_serialization_coreDeserializationStrategy>)deserializer __attribute__((swift_name("decodeSerializableValue(deserializer:)")));
- (int16_t)decodeShort __attribute__((swift_name("decodeShort()")));
- (NSString *)decodeString __attribute__((swift_name("decodeString()")));
@property (readonly) SharedKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("KotlinByteIterator")))
@interface SharedKotlinByteIterator : SharedBase <SharedKotlinIterator>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedByte *)next __attribute__((swift_name("next()")));
- (int8_t)nextByte __attribute__((swift_name("nextByte()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreKind")))
@interface SharedKoin_coreKind : SharedKotlinEnum<SharedKoin_coreKind *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKoin_coreKind *singleton __attribute__((swift_name("singleton")));
@property (class, readonly) SharedKoin_coreKind *factory __attribute__((swift_name("factory")));
@property (class, readonly) SharedKoin_coreKind *scoped __attribute__((swift_name("scoped")));
+ (SharedKotlinArray<SharedKoin_coreKind *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKoin_coreKind *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreCallbacks")))
@interface SharedKoin_coreCallbacks<T> : SharedBase
- (instancetype)initWithOnClose:(void (^ _Nullable)(T _Nullable))onClose __attribute__((swift_name("init(onClose:)"))) __attribute__((objc_designated_initializer));
- (SharedKoin_coreCallbacks<T> *)doCopyOnClose:(void (^ _Nullable)(T _Nullable))onClose __attribute__((swift_name("doCopy(onClose:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) void (^ _Nullable onClose)(T _Nullable) __attribute__((swift_name("onClose")));
@end

__attribute__((swift_name("Koin_coreKoinExtension")))
@protocol SharedKoin_coreKoinExtension
@required
- (void)onClose __attribute__((swift_name("onClose()")));
- (void)onRegisterKoin:(SharedKoin_coreKoin *)koin __attribute__((swift_name("onRegister(koin:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin_coreScopeRegistry.Companion")))
@interface SharedKoin_coreScopeRegistryCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoin_coreScopeRegistryCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreCompositeEncoder")))
@protocol SharedKotlinx_serialization_coreCompositeEncoder
@required
- (void)encodeBooleanElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(BOOL)value __attribute__((swift_name("encodeBooleanElement(descriptor:index:value:)")));
- (void)encodeByteElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int8_t)value __attribute__((swift_name("encodeByteElement(descriptor:index:value:)")));
- (void)encodeCharElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(unichar)value __attribute__((swift_name("encodeCharElement(descriptor:index:value:)")));
- (void)encodeDoubleElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(double)value __attribute__((swift_name("encodeDoubleElement(descriptor:index:value:)")));
- (void)encodeFloatElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(float)value __attribute__((swift_name("encodeFloatElement(descriptor:index:value:)")));
- (id<SharedKotlinx_serialization_coreEncoder>)encodeInlineElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("encodeInlineElement(descriptor:index:)")));
- (void)encodeIntElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int32_t)value __attribute__((swift_name("encodeIntElement(descriptor:index:value:)")));
- (void)encodeLongElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int64_t)value __attribute__((swift_name("encodeLongElement(descriptor:index:value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNullableSerializableElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index serializer:(id<SharedKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeNullableSerializableElement(descriptor:index:serializer:value:)")));
- (void)encodeSerializableElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index serializer:(id<SharedKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeSerializableElement(descriptor:index:serializer:value:)")));
- (void)encodeShortElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int16_t)value __attribute__((swift_name("encodeShortElement(descriptor:index:value:)")));
- (void)encodeStringElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(NSString *)value __attribute__((swift_name("encodeStringElement(descriptor:index:value:)")));
- (void)endStructureDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("endStructure(descriptor:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)shouldEncodeElementDefaultDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("shouldEncodeElementDefault(descriptor:index:)")));
@property (readonly) SharedKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerializersModule")))
@interface SharedKotlinx_serialization_coreSerializersModule : SharedBase

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)dumpToCollector:(id<SharedKotlinx_serialization_coreSerializersModuleCollector>)collector __attribute__((swift_name("dumpTo(collector:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<SharedKotlinx_serialization_coreKSerializer> _Nullable)getContextualKClass:(id<SharedKotlinKClass>)kClass typeArgumentsSerializers:(NSArray<id<SharedKotlinx_serialization_coreKSerializer>> *)typeArgumentsSerializers __attribute__((swift_name("getContextual(kClass:typeArgumentsSerializers:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<SharedKotlinx_serialization_coreSerializationStrategy> _Nullable)getPolymorphicBaseClass:(id<SharedKotlinKClass>)baseClass value:(id)value __attribute__((swift_name("getPolymorphic(baseClass:value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<SharedKotlinx_serialization_coreDeserializationStrategy> _Nullable)getPolymorphicBaseClass:(id<SharedKotlinKClass>)baseClass serializedClassName:(NSString * _Nullable)serializedClassName __attribute__((swift_name("getPolymorphic(baseClass:serializedClassName:)")));
@end

__attribute__((swift_name("KotlinAnnotation")))
@protocol SharedKotlinAnnotation
@required
@end


/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
__attribute__((swift_name("Kotlinx_serialization_coreSerialKind")))
@interface SharedKotlinx_serialization_coreSerialKind : SharedBase
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreCompositeDecoder")))
@protocol SharedKotlinx_serialization_coreCompositeDecoder
@required
- (BOOL)decodeBooleanElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeBooleanElement(descriptor:index:)")));
- (int8_t)decodeByteElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeByteElement(descriptor:index:)")));
- (unichar)decodeCharElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeCharElement(descriptor:index:)")));
- (int32_t)decodeCollectionSizeDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeCollectionSize(descriptor:)")));
- (double)decodeDoubleElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeDoubleElement(descriptor:index:)")));
- (int32_t)decodeElementIndexDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeElementIndex(descriptor:)")));
- (float)decodeFloatElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeFloatElement(descriptor:index:)")));
- (id<SharedKotlinx_serialization_coreDecoder>)decodeInlineElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeInlineElement(descriptor:index:)")));
- (int32_t)decodeIntElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeIntElement(descriptor:index:)")));
- (int64_t)decodeLongElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeLongElement(descriptor:index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id _Nullable)decodeNullableSerializableElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index deserializer:(id<SharedKotlinx_serialization_coreDeserializationStrategy>)deserializer previousValue:(id _Nullable)previousValue __attribute__((swift_name("decodeNullableSerializableElement(descriptor:index:deserializer:previousValue:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)decodeSequentially __attribute__((swift_name("decodeSequentially()")));
- (id _Nullable)decodeSerializableElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index deserializer:(id<SharedKotlinx_serialization_coreDeserializationStrategy>)deserializer previousValue:(id _Nullable)previousValue __attribute__((swift_name("decodeSerializableElement(descriptor:index:deserializer:previousValue:)")));
- (int16_t)decodeShortElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeShortElement(descriptor:index:)")));
- (NSString *)decodeStringElementDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeStringElement(descriptor:index:)")));
- (void)endStructureDescriptor:(id<SharedKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("endStructure(descriptor:)")));
@property (readonly) SharedKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinNothing")))
@interface SharedKotlinNothing : SharedBase
@end


/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
__attribute__((swift_name("Kotlinx_serialization_coreSerializersModuleCollector")))
@protocol SharedKotlinx_serialization_coreSerializersModuleCollector
@required
- (void)contextualKClass:(id<SharedKotlinKClass>)kClass provider:(id<SharedKotlinx_serialization_coreKSerializer> (^)(NSArray<id<SharedKotlinx_serialization_coreKSerializer>> *))provider __attribute__((swift_name("contextual(kClass:provider:)")));
- (void)contextualKClass:(id<SharedKotlinKClass>)kClass serializer:(id<SharedKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("contextual(kClass:serializer:)")));
- (void)polymorphicBaseClass:(id<SharedKotlinKClass>)baseClass actualClass:(id<SharedKotlinKClass>)actualClass actualSerializer:(id<SharedKotlinx_serialization_coreKSerializer>)actualSerializer __attribute__((swift_name("polymorphic(baseClass:actualClass:actualSerializer:)")));
- (void)polymorphicDefaultBaseClass:(id<SharedKotlinKClass>)baseClass defaultDeserializerProvider:(id<SharedKotlinx_serialization_coreDeserializationStrategy> _Nullable (^)(NSString * _Nullable))defaultDeserializerProvider __attribute__((swift_name("polymorphicDefault(baseClass:defaultDeserializerProvider:)"))) __attribute__((deprecated("Deprecated in favor of function with more precise name: polymorphicDefaultDeserializer")));
- (void)polymorphicDefaultDeserializerBaseClass:(id<SharedKotlinKClass>)baseClass defaultDeserializerProvider:(id<SharedKotlinx_serialization_coreDeserializationStrategy> _Nullable (^)(NSString * _Nullable))defaultDeserializerProvider __attribute__((swift_name("polymorphicDefaultDeserializer(baseClass:defaultDeserializerProvider:)")));
- (void)polymorphicDefaultSerializerBaseClass:(id<SharedKotlinKClass>)baseClass defaultSerializerProvider:(id<SharedKotlinx_serialization_coreSerializationStrategy> _Nullable (^)(id))defaultSerializerProvider __attribute__((swift_name("polymorphicDefaultSerializer(baseClass:defaultSerializerProvider:)")));
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
